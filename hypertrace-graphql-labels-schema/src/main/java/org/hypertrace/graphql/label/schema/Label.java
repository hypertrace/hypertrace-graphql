package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import javax.annotation.Nullable;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(Label.TYPE_NAME)
public interface Label extends Identifiable {
  String TYPE_NAME = "Label";
  String ARGUMENT_NAME = "label";
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
