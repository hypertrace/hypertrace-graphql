package org.hypertrace.core.graphql.schema.registry;

import static java.util.Objects.nonNull;

import graphql.annotations.AnnotationsSchemaCreator;
import graphql.annotations.processor.GraphQLAnnotations;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLCodeRegistry.Builder;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.visibility.NoIntrospectionGraphqlFieldVisibility;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hypertrace.core.graphql.spi.config.GraphQlEndpointConfig;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

class GraphQlAnnotatedSchemaMerger implements Provider<GraphQLSchema> {

  private final GraphQlSchemaRegistry registry;
  private final GraphQlEndpointConfig config;

  @Inject
  GraphQlAnnotatedSchemaMerger(GraphQlSchemaRegistry registry, GraphQlEndpointConfig config) {
    this.registry = registry;
    this.config = config;
  }

  @Override
  public GraphQLSchema get() {
    GraphQlSchemaFragment rootFragment = this.registry.getRootFragment();
    Set<GraphQlSchemaFragment> fragments = this.registry.getRegisteredFragments();
    GraphQLAnnotations annotationProcessor = new GraphQLAnnotations();

    this.registerAllTypeFunctions(fragments, annotationProcessor);

    return fragments.stream()
        .map(fragment -> this.fragmentToSchema(fragment, annotationProcessor))
        .reduce(this.fragmentToSchema(rootFragment, annotationProcessor), this::merge)
        .transform(this::removeRootPlaceholders)
        .transform(this::applyConfig);
  }

  private void registerAllTypeFunctions(
      Collection<GraphQlSchemaFragment> fragments, GraphQLAnnotations annotationProcessor) {
    fragments.stream()
        .map(GraphQlSchemaFragment::typeFunctions)
        .flatMap(Collection::stream)
        .forEach(annotationProcessor::registerTypeFunction);
  }

  private GraphQLSchema fragmentToSchema(
      GraphQlSchemaFragment fragment, GraphQLAnnotations annotationProcessor) {
    AnnotationsSchemaCreator.Builder builder =
        AnnotationsSchemaCreator.newAnnotationsSchema()
            .setAnnotationsProcessor(annotationProcessor)
            .mutation(fragment.annotatedMutationClass());

    // Query must be assigned, use our root query object as a placeholder if our fragment defines a
    // mutation only
    if (nonNull(fragment.annotatedQueryClass())) {
      builder.query(fragment.annotatedQueryClass());
    } else {
      builder.query(this.registry.getRootFragment().annotatedQueryClass());
    }

    return builder.build();
  }

  private GraphQLSchema merge(GraphQLSchema accumulatedSchema, GraphQLSchema schemaToAdd) {
    return GraphQLSchema.newSchema()
        .query(this.mergeObjects(accumulatedSchema.getQueryType(), schemaToAdd.getQueryType()))
        .mutation(
            this.mergeObjects(accumulatedSchema.getMutationType(), schemaToAdd.getMutationType()))
        .codeRegistry(
            this.mergeCodeRegistries(
                this.buildMapWithoutNulls(
                    Set.of(
                        new SimpleImmutableEntry<>(
                            schemaToAdd.getQueryType(), accumulatedSchema.getQueryType()),
                        new SimpleImmutableEntry<>(
                            schemaToAdd.getMutationType(), accumulatedSchema.getMutationType()))),
                accumulatedSchema.getCodeRegistry(),
                schemaToAdd.getCodeRegistry()))
        .build();
  }

  @Nullable
  private GraphQLObjectType mergeObjects(
      @NonNull GraphQLObjectType accumulatedObject, @Nullable GraphQLObjectType objectToAdd) {

    if (objectToAdd == null) {
      return accumulatedObject;
    }

    return GraphQLObjectType.newObject(accumulatedObject)
        .fields(objectToAdd.getFieldDefinitions())
        .build();
  }

  /*
   The remapping of objects accounts for the fact that data fetchers are scoped to their parent
   object name. When we merge objects, their name can change and we need to put in a new pointer to
   the data fetcher for that new name.
  */
  private GraphQLCodeRegistry mergeCodeRegistries(
      Map<GraphQLObjectType, GraphQLObjectType> remappedObjects,
      GraphQLCodeRegistry accumulatingRegistry,
      GraphQLCodeRegistry registryToMerge) {

    Builder updatedRegistryBuilder =
        GraphQLCodeRegistry.newCodeRegistry(accumulatingRegistry)
            .typeResolvers(registryToMerge)
            .dataFetchers(registryToMerge);
    remappedObjects.forEach(
        (objectToMerge, accumulatingObject) ->
            objectToMerge
                .getFieldDefinitions()
                .forEach(
                    graphQLFieldDefinition ->
                        updatedRegistryBuilder.dataFetcher(
                            accumulatingObject,
                            graphQLFieldDefinition,
                            registryToMerge.getDataFetcher(
                                objectToMerge, graphQLFieldDefinition))));

    return updatedRegistryBuilder.build();
  }

  private Map<GraphQLObjectType, GraphQLObjectType> buildMapWithoutNulls(
      Collection<Entry<GraphQLObjectType, GraphQLObjectType>> entries) {
    return entries.stream()
        .filter(entry -> nonNull(entry) && nonNull(entry.getKey()) && nonNull(entry.getValue()))
        .collect(Collectors.toUnmodifiableMap(Entry::getKey, Entry::getValue));
  }

  private void removeRootPlaceholders(GraphQLSchema.Builder mergedSchema) {
    GraphQLSchema mergedWithPlaceholders = mergedSchema.build();
    // Queries must be defined, mutation is optional which is represented by a null
    mergedSchema.query(
        this.rebuildTypeWithoutPlaceholders(mergedWithPlaceholders.getQueryType()).orElseThrow());
    mergedSchema.mutation(
        this.rebuildTypeWithoutPlaceholders(mergedWithPlaceholders.getMutationType()).orElse(null));
  }

  private void applyConfig(GraphQLSchema.Builder builder) {
    if (!this.config.isIntrospectionAllowed()) {
      GraphQLSchema schemaSoFar = builder.build();
      GraphQLCodeRegistry registryWithoutIntrospection =
          schemaSoFar
              .getCodeRegistry()
              .transform(
                  registry ->
                      registry.fieldVisibility(
                          NoIntrospectionGraphqlFieldVisibility.NO_INTROSPECTION_FIELD_VISIBILITY));
      builder.codeRegistry(registryWithoutIntrospection);
    }
  }

  private Optional<GraphQLObjectType> rebuildTypeWithoutPlaceholders(GraphQLObjectType objectType) {
    // Schema validation now requires all objects to have at least one field. To merge partial
    // fragments, we start with placeholders, identified by a constant, then remove them at the end.
    GraphQLObjectType newType =
        GraphQLObjectType.newObject(objectType)
            .replaceFields(
                objectType.getFields().stream()
                    .filter(
                        field ->
                            Optional.ofNullable(field.getDescription())
                                .map(
                                    description ->
                                        !description.equals(DefaultSchema.PLACEHOLDER_DESCRIPTION))
                                .orElse(true))
                    .collect(Collectors.toList()))
            .build();

    if (newType.getFields().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(newType);
  }
}
