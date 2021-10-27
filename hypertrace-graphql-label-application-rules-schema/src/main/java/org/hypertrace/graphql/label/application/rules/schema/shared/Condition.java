package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLUnion;

@GraphQLName(Condition.TYPE_NAME)
@GraphQLUnion(possibleTypes = {LeafCondition.class, CompositeCondition.class})
public interface Condition {
  String TYPE_NAME = "Condition";
}
