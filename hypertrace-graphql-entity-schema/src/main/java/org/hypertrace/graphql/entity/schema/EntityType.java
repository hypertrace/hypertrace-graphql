package org.hypertrace.graphql.entity.schema;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(EntityType.TYPE_NAME)
public interface EntityType {
  String TYPE_NAME = "EntityType";
}
