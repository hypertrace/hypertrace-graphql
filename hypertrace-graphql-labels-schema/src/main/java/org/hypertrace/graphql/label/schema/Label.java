package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(Label.TYPE_NAME)
public interface Label extends Identifiable {
  String TYPE_NAME = "Label";
  String ARGUMENT_NAME = "label";
  String KEY = "key";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(KEY)
  String key();
}
