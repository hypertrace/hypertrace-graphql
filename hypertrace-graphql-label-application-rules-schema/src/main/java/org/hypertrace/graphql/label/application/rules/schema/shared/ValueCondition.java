package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLUnion;

@GraphQLName(ValueCondition.TYPE_NAME)
@GraphQLUnion(possibleTypes = {StringCondition.class, UnaryCondition.class})
public interface ValueCondition {
  String TYPE_NAME = "ValueCondition";
}
