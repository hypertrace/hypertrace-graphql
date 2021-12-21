package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.LimitArgument;

@GraphQLName(LabeledEntities.TYPE_NAME)
public interface LabeledEntities {
  String TYPE_NAME = "LabeledEntities";
  String LABELED_ENTITIES_QUERY_NAME = "labeledEntities";
  String ENTITY_TYPE_ARGUMENT_NAME = "type";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LABELED_ENTITIES_QUERY_NAME)
  LabeledEntityResultSet labeledEntities(
      @GraphQLNonNull @GraphQLName(ENTITY_TYPE_ARGUMENT_NAME) String entityType,
      @GraphQLName(LimitArgument.ARGUMENT_NAME) int limit);
}
