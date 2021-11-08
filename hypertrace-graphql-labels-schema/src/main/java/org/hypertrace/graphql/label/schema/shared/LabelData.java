package org.hypertrace.graphql.label.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import javax.annotation.Nullable;

@GraphQLName(LabelData.TYPE_NAME)
public interface LabelData {
  String TYPE_NAME = "LabelData";

  String LABEL_NAME_KEY = "key";
  String COLOR_KEY = "color";
  String DESCRIPTION_KEY = "description";

  @GraphQLField()
  @GraphQLName(LABEL_NAME_KEY)
  @GraphQLNonNull()
  String key();

  @GraphQLField()
  @GraphQLName(COLOR_KEY)
  @Nullable
  String color();

  @GraphQLField()
  @GraphQLName(DESCRIPTION_KEY)
  @Nullable
  String description();
}
