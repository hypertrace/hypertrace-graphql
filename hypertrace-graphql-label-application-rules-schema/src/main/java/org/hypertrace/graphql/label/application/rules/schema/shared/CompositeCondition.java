package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;

@GraphQLName(CompositeCondition.TYPE_NAME)
public interface CompositeCondition extends Condition {
  String TYPE_NAME = "CompositeCondition";

  String LOGICAL_OPERATOR_KEY = "LogicalOperator";
  String CHILDREN_KEY = "ChildConditions";

  @GraphQLName(StringCondition.Operator.TYPE_NAME)
  enum LogicalOperator {
    LOGICAL_OPERATOR_AND,
    LOGICAL_OPERATOR_OR;
    static final String TYPE_NAME = "LogicalOperator";
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
