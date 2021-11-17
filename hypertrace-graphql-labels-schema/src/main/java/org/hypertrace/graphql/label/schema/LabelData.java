package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import javax.annotation.Nullable;

public interface LabelData {
  String KEY = "key";
  String COLOR_KEY = "color";
  String DESCRIPTION_KEY = "description";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(KEY)
  String key();

  @GraphQLField
  @Nullable
  @GraphQLName(COLOR_KEY)
  String color();

  @GraphQLField
  @Nullable
  @GraphQLName(DESCRIPTION_KEY)
  String description();
}
