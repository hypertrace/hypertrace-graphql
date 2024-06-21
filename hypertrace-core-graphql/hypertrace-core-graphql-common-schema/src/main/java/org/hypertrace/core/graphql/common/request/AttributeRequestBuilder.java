package org.hypertrace.core.graphql.common.request;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.stream.Stream;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface AttributeRequestBuilder {

  Single<AttributeRequest> buildForId(GraphQlRequestContext context, String attributeModelScope);

  Observable<AttributeRequest> buildForAttributeQueryableSelectionSet(
      GraphQlRequestContext context,
      String attributeScope,
      DataFetchingFieldSelectionSet attributeQueryableSelectionSet);

  Observable<AttributeRequest> buildForAttributeQueryableFields(
      GraphQlRequestContext context,
      String attributeModelScope,
      Stream<SelectedField> attributeQueryableFields);

  Observable<AttributeRequest> buildForAttributeQueryableFieldsAndId(
      GraphQlRequestContext context,
      String attributeScope,
      Stream<SelectedField> attributeQueryableFields);

  Single<AttributeRequest> buildForAttributeExpression(
      GraphQlRequestContext context,
      String attributeScope,
      AttributeExpression attributeExpression);

  AttributeRequest buildForAttribute(AttributeModel attribute);
}
