package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(LabelApplicationRule.TYPE_NAME)
public interface LabelApplicationRule extends Identifiable {
  String TYPE_NAME = "LabelApplicationRule";
  String ARGUMENT_NAME = "labelApplicationRule";

  String LABEL_APPLICATION_RULE_DATA_KEY = "labelApplicationRuleData";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LABEL_APPLICATION_RULE_DATA_KEY)
  LabelApplicationRuleData labelApplicationRuleData();
}
