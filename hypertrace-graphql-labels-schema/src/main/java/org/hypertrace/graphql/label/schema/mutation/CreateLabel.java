package org.hypertrace.graphql.label.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(CreateLabel.TYPE_NAME)
public interface CreateLabel {
  String TYPE_NAME = "CreateLabel";
  String ARGUMENT_NAME = "label";
  String KEY = "key";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(KEY)
  String key();
}
