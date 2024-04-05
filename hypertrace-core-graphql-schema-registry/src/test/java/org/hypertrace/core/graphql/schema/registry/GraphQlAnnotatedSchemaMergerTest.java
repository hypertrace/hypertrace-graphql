package org.hypertrace.core.graphql.schema.registry;

import static org.hypertrace.core.graphql.schema.registry.DefaultSchema.ROOT_MUTATION_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import graphql.Scalars;
import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.processor.ProcessingElementsContainer;
import graphql.annotations.processor.typeFunctions.TypeFunction;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.visibility.NoIntrospectionGraphqlFieldVisibility;
import java.lang.reflect.AnnotatedType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.hypertrace.core.graphql.spi.config.GraphQlEndpointConfig;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GraphQlAnnotatedSchemaMergerTest {

  @Mock private GraphQlSchemaRegistry mockRegistry;
  @Mock private DataFetchingEnvironment mockDataFetchingEnvironment;
  @Mock private GraphQlEndpointConfig mockConfig;

  private GraphQlAnnotatedSchemaMerger merger;

  interface FirstQuerySchema {
    @GraphQLField
    String first();

    @GraphQLField
    SharedType second();
  }

  interface SecondQuerySchema {
    @GraphQLField
    SharedType third();
  }

  interface ThirdQuerySchema {
    @GraphQLDataFetcher(CustomDataFetcher.class)
    @GraphQLField
    String fourth();
  }

  interface FirstMutationSchema {
    @GraphQLField
    SharedType mutateOne(String argument);
  }

  interface SecondMutationSchema {
    @GraphQLField
    SharedType mutateTwo(String argument);
  }

  public static class CustomDataFetcher implements DataFetcher<String> {

    @Override
    public String get(DataFetchingEnvironment environment) {
      return "custom";
    }
  }

  interface SharedType {
    @GraphQLField
    @GraphQLDataFetcher(CustomDataFetcher.class)
    String sharedValue();
  }

  @BeforeEach
  public void beforeEach() {
    this.merger = new GraphQlAnnotatedSchemaMerger(mockRegistry, mockConfig);

    when(mockRegistry.getRootFragment()).thenReturn(new DefaultSchema());
  }

  @Test
  void mergesSingleSchema() throws Exception {
    when(mockRegistry.getRegisteredFragments())
        .thenReturn(Set.of(this.createSchemaFragment(FirstQuerySchema.class)));

    GraphQLSchema schema = this.merger.get();
    this.verifySchemaWithQueryFields(schema, Set.of("first", "second"));
    this.verifySchemaWithMutationFields(schema, Set.of());

    GraphQLObjectType sharedType = (GraphQLObjectType) schema.getType("SharedType");
    assertEquals(
        "custom",
        schema
            .getCodeRegistry()
            .getDataFetcher(sharedType, sharedType.getFieldDefinition("sharedValue"))
            .get(mockDataFetchingEnvironment));
  }

  @Test
  void mergesMultipleSchemas() throws Exception {
    when(mockRegistry.getRegisteredFragments())
        .thenReturn(
            Set.of(
                this.createSchemaFragment(FirstQuerySchema.class),
                this.createSchemaFragment(SecondQuerySchema.class),
                this.createSchemaFragment(ThirdQuerySchema.class)));

    GraphQLSchema schema = this.merger.get();
    this.verifySchemaWithQueryFields(schema, Set.of("first", "second", "third", "fourth"));

    GraphQLFieldDefinition customFetcherField = schema.getQueryType().getFieldDefinition("fourth");
    assertEquals(
        "custom",
        schema
            .getCodeRegistry()
            .getDataFetcher(schema.getQueryType(), customFetcherField)
            .get(mockDataFetchingEnvironment));
  }

  @Test
  void mergesMutationSchemas() {
    when(mockRegistry.getRegisteredFragments())
        .thenReturn(
            Set.of(
                this.createSchemaFragment(FirstQuerySchema.class, FirstMutationSchema.class),
                this.createSchemaFragment(SecondQuerySchema.class, SecondMutationSchema.class)));

    this.verifySchemaWithMutationFields(this.merger.get(), Set.of("mutateOne", "mutateTwo"));
  }

  @Test
  void supportsMutationOnlyFragment() {
    when(mockRegistry.getRegisteredFragments())
        .thenReturn(
            Set.of(
                this.createSchemaFragment(FirstQuerySchema.class),
                this.createSchemaFragment(null, FirstMutationSchema.class)));

    this.verifySchemaWithMutationFields(this.merger.get(), Set.of("mutateOne"));
    this.verifySchemaWithQueryFields(this.merger.get(), Set.of("first", "second"));
  }

  @Test
  void supportsTypeFunctions() {
    TypeFunction typeFunction =
        new TypeFunction() {
          @Override
          public boolean canBuildType(Class<?> aClass, AnnotatedType annotatedType) {
            return aClass == SharedType.class;
          }

          @Override
          public GraphQLType buildType(
              boolean input,
              Class<?> aClass,
              AnnotatedType annotatedType,
              ProcessingElementsContainer container) {
            return GraphQLObjectType.newObject()
                .name("typeFunctionType")
                .field(
                    GraphQLFieldDefinition.newFieldDefinition()
                        .name("typeFunctionTypeField")
                        .type(Scalars.GraphQLString))
                .build();
          }
        };

    when(mockRegistry.getRegisteredFragments())
        .thenReturn(
            Set.of(this.createSchemaFragment(FirstQuerySchema.class, null, List.of(typeFunction))));

    GraphQLSchema schema = this.merger.get();
    this.verifySchemaWithQueryFields(schema, Set.of("first", "second"));
    assertEquals(
        "typeFunctionType",
        ((GraphQLObjectType) schema.getQueryType().getFieldDefinition("second").getType())
            .getName());
  }

  @Test
  void supportsEnablingIntrospection() {
    when(mockRegistry.getRegisteredFragments())
        .thenReturn(Set.of(this.createSchemaFragment(FirstQuerySchema.class)));
    when(mockConfig.isIntrospectionAllowed()).thenReturn(true);
    GraphQLSchema schema = this.merger.get();
    assertNotNull(schema.getIntrospectionSchemaType());
    assertFalse(
        schema.getCodeRegistry().getFieldVisibility()
            instanceof NoIntrospectionGraphqlFieldVisibility);
  }

  @Test
  void supportsDisablingIntrospection() {
    when(mockRegistry.getRegisteredFragments())
        .thenReturn(Set.of(this.createSchemaFragment(FirstQuerySchema.class)));
    when(mockConfig.isIntrospectionAllowed()).thenReturn(false);
    GraphQLSchema schema = this.merger.get();
    assertInstanceOf(
        NoIntrospectionGraphqlFieldVisibility.class, schema.getCodeRegistry().getFieldVisibility());
  }

  private GraphQlSchemaFragment createSchemaFragment(Class<?> queryClass) {
    return this.createSchemaFragment(queryClass, null);
  }

  private GraphQlSchemaFragment createSchemaFragment(Class<?> queryClass, Class<?> mutationClass) {
    return this.createSchemaFragment(queryClass, mutationClass, List.of());
  }

  private GraphQlSchemaFragment createSchemaFragment(
      Class<?> queryClass, Class<?> mutationClass, List<TypeFunction> typeFunctionList) {
    return new GraphQlSchemaFragment() {
      @Override
      public String fragmentName() {
        return queryClass.getSimpleName();
      }

      @Override
      public Class<?> annotatedQueryClass() {
        return queryClass;
      }

      @Override
      public Class<?> annotatedMutationClass() {
        return mutationClass;
      }

      @Nonnull
      @Override
      public List<TypeFunction> typeFunctions() {
        return typeFunctionList;
      }
    };
  }

  private void verifySchemaWithQueryFields(GraphQLSchema schema, Set<String> fields) {
    assertEquals(DefaultSchema.ROOT_QUERY_NAME, schema.getQueryType().getName());
    List<GraphQLFieldDefinition> fieldDefinitions = schema.getQueryType().getFieldDefinitions();
    assertEquals(fields.size(), fieldDefinitions.size());
    assertTrue(
        fields.containsAll(
            fieldDefinitions.stream()
                .map(GraphQLFieldDefinition::getName)
                .collect(Collectors.toUnmodifiableSet())));
  }

  private void verifySchemaWithMutationFields(GraphQLSchema schema, Set<String> fields) {
    if (fields.isEmpty()) {
      // A type must have fields, so if no fields expected, no type expected
      assertNull(schema.getMutationType());
    } else {
      assertEquals(ROOT_MUTATION_NAME, schema.getMutationType().getName());
      List<GraphQLFieldDefinition> fieldDefinitions =
          schema.getMutationType().getFieldDefinitions();
      assertEquals(fields.size(), fieldDefinitions.size());
      assertTrue(
          fields.containsAll(
              fieldDefinitions.stream()
                  .map(GraphQLFieldDefinition::getName)
                  .collect(Collectors.toUnmodifiableSet())));
    }
  }
}
