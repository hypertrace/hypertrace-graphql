package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;
import org.hypertrace.core.graphql.common.schema.results.arguments.page.LimitArgument;
import org.hypertrace.graphql.label.schema.rule.LabelApplicationRuleResultSet;

@GraphQLName(Label.TYPE_NAME)
public interface Label extends Identifiable, LabelData {
  String TYPE_NAME = "Label";
  String ARGUMENT_NAME = "label";
  String LABELED_ENTITIES_QUERY_NAME = "labeledEntities";
  String ENTITY_TYPE_ARGUMENT_NAME = "type";
  String LABEL_APPLICATION_RULES_QUERY_NAME = "labelApplicationRules";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LABELED_ENTITIES_QUERY_NAME)
  LabeledEntityResultSet labeledEntities(
      @GraphQLNonNull @GraphQLName(ENTITY_TYPE_ARGUMENT_NAME) String entityType,
      @GraphQLName(LimitArgument.ARGUMENT_NAME) Integer limit);

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LABEL_APPLICATION_RULES_QUERY_NAME)
  LabelApplicationRuleResultSet labelApplicationRules(
      @GraphQLName(LimitArgument.ARGUMENT_NAME) Integer limit);
}
