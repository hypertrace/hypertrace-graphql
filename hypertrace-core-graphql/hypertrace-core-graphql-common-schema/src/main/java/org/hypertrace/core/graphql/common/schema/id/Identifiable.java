package org.hypertrace.core.graphql.common.schema.id;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

public interface Identifiable {

  String IDENTITY_FIELD_NAME = "id";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(IDENTITY_FIELD_NAME)
  String id();
}
