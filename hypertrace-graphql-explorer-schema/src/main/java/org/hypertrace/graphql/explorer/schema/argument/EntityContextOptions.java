package org.hypertrace.graphql.explorer.schema.argument;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(EntityContextOptions.TYPE_NAME)
public interface EntityContextOptions {
  String TYPE_NAME = "EntityContextOptions";
  String ARGUMENT_NAME = "entityContextOptions";
  String INCLUDE_NON_LIVE_ENTITIES = "includeNonLiveEntities";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(INCLUDE_NON_LIVE_ENTITIES)
  boolean includeNonLiveEntities();
}
