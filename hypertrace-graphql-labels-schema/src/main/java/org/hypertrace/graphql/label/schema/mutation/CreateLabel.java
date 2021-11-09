package org.hypertrace.graphql.label.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.label.schema.shared.LabelData;

@GraphQLName(CreateLabel.TYPE_NAME)
public interface CreateLabel {
  String TYPE_NAME = "CreateLabel";
  String ARGUMENT_NAME = "createLabel";

  String LABEL_DATA_KEY = "data";

  @GraphQLField()
  @GraphQLName(LABEL_DATA_KEY)
  @GraphQLNonNull()
  LabelData data();
}
