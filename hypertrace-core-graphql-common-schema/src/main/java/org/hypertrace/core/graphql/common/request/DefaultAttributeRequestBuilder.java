package org.hypertrace.core.graphql.common.request;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeQueryable;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeKeyArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;

class DefaultAttributeRequestBuilder implements AttributeRequestBuilder {

  private final AttributeStore attributeStore;
  private final ArgumentDeserializer argumentDeserializer;
  private final GraphQlSelectionFinder selectionFinder;

  @Inject
  DefaultAttributeRequestBuilder(
      AttributeStore attributeStore,
      ArgumentDeserializer argumentDeserializer,
      GraphQlSelectionFinder selectionFinder) {
    this.attributeStore = attributeStore;
    this.argumentDeserializer = argumentDeserializer;
    this.selectionFinder = selectionFinder;
  }

  @Override
  public Single<AttributeRequest> buildForId(GraphQlRequestContext context, String attributeScope) {
    return this.attributeStore.getIdAttribute(context, attributeScope).map(this::buildForAttribute);
  }

  @Override
  public Observable<AttributeRequest> buildForAttributeQueryableSelectionSet(
      GraphQlRequestContext context,
      String attributeScope,
      DataFetchingFieldSelectionSet attributeQueryableSelectionSet) {
    return Observable.fromStream(
            this.getAttributeKeysForAttributeQueryableSelectionSet(attributeQueryableSelectionSet))
        .flatMapSingle(key -> this.buildForKey(context, attributeScope, key))
        .distinct();
  }

  @Override
  public Observable<AttributeRequest> buildForAttributeQueryableFields(
      GraphQlRequestContext context,
      String attributeScope,
      Stream<SelectedField> attributeQueryableFields) {
    return Observable.fromStream(attributeQueryableFields)
        .map(SelectedField::getSelectionSet)
        .flatMap(
            selectionSet ->
                this.buildForAttributeQueryableSelectionSet(context, attributeScope, selectionSet))
        .distinct();
  }

  @Override
  public Observable<AttributeRequest> buildForAttributeQueryableFieldsAndId(
      GraphQlRequestContext context,
      String attributeScope,
      Stream<SelectedField> attributeQueryableFields) {
    return this.buildForAttributeQueryableFields(context, attributeScope, attributeQueryableFields)
        .mergeWith(this.buildForId(context, attributeScope))
        .distinct();
  }

  @Override
  public Single<AttributeRequest> buildForKey(
      GraphQlRequestContext context, String requestScope, String attributeKey) {
    return this.attributeStore
        .get(context, requestScope, attributeKey)
        .map(this::buildForAttribute);
  }

  @Override
  public AttributeRequest buildForAttribute(AttributeModel attribute) {
    return new DefaultAttributeRequest(attribute);
  }

  private Stream<String> getAttributeKeysForAttributeQueryableSelectionSet(
      DataFetchingFieldSelectionSet selectionSet) {
    return this.selectionFinder
        .findSelections(
            selectionSet, SelectionQuery.namedChild(AttributeQueryable.ATTRIBUTE_FIELD_NAME))
        .flatMap(this::getArgument);
  }

  private Stream<String> getArgument(SelectedField attributeField) {
    return this.argumentDeserializer
        .deserializePrimitive(attributeField.getArguments(), AttributeKeyArgument.class)
        .stream();
  }

  @Value
  @Accessors(fluent = true)
  static class DefaultAttributeRequest implements AttributeRequest {
    AttributeModel attribute;

    @Override
    public String alias() {
      return attribute.id();
    }
  }
}
