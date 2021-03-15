package org.hypertrace.graphql.entity.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.entity.schema.argument.EntityTypeArgument;

public interface EntityJoinable {
  String ENTITY_KEY = "entity";

  @GraphQLField
  @GraphQLName(ENTITY_KEY)
  Entity entity(@GraphQLNonNull @GraphQLName(EntityTypeArgument.ARGUMENT_NAME) String entityType);
}
