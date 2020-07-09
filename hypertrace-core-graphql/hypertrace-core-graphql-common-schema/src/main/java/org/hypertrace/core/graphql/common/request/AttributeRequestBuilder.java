package org.hypertrace.core.graphql.common.request;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.stream.Stream;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface AttributeRequestBuilder {

  Single<AttributeRequest> buildForId(
      GraphQlRequestContext context, AttributeModelScope attributeModelScope);

  Observable<AttributeRequest> buildForAttributeQueryableSelectionSet(
      GraphQlRequestContext context,
      AttributeModelScope attributeScope,
      DataFetchingFieldSelectionSet attributeQueryableSelectionSet);

  Observable<AttributeRequest> buildForAttributeQueryableFields(
      GraphQlRequestContext context,
      AttributeModelScope attributeModelScope,
      Stream<SelectedField> attributeQueryableFields);

  Observable<AttributeRequest> buildForAttributeQueryableFieldsAndId(
      GraphQlRequestContext context,
      AttributeModelScope attributeScope,
      Stream<SelectedField> attributeQueryableFields);

  Single<AttributeRequest> buildForKey(
      GraphQlRequestContext context, AttributeModelScope attributeModelScope, String attributeKey);

  AttributeRequest buildForAttribute(AttributeModel attribute);
}
