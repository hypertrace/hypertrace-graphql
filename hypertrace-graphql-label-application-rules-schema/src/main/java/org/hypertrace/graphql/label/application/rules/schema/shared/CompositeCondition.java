package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;

@GraphQLName(CompositeCondition.TYPE_NAME)
public interface CompositeCondition {
  String TYPE_NAME = "LabelApplicationCompositeCondition";

  String LOGICAL_OPERATOR_KEY = "operator";
  String CHILDREN_KEY = "children";

  @GraphQLName(LogicalOperator.TYPE_NAME)
  enum LogicalOperator {
    LOGICAL_OPERATOR_AND,
    LOGICAL_OPERATOR_OR;
    private static final String TYPE_NAME = "LogicalOperator";
  }

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LOGICAL_OPERATOR_KEY)
  LogicalOperator operator();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(CHILDREN_KEY)
  List<LeafCondition> children();
}
