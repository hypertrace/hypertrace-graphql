package org.hypertrace.core.graphql.common.schema.attributes;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(AttributeScope.TYPE_NAME)
public interface AttributeScope {
  // Should be provided for the specific attributes available
  String TYPE_NAME = "AttributeScope";

  // Temporary measure until the scope enum is removed entirely
  String getScopeString();
}
