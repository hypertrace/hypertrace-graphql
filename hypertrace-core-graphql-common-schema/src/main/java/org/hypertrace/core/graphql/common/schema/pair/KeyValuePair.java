package org.hypertrace.core.graphql.common.schema.pair;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(KeyValuePair.TYPE_NAME)
@GraphQLDescription("A pair of a key and a value")
public interface KeyValuePair {
  String TYPE_NAME = "KeyValuePair";

  String KEY = "key";
  String VALUE = "value";

  @GraphQLField
  @GraphQLName(KEY)
  @GraphQLNonNull
  @GraphQLDescription("String based key name")
  String key();

  @GraphQLField
  @GraphQLName(VALUE)
  @GraphQLNonNull
  @GraphQLDescription("Value of a generic type")
  Object value();
}
