package org.hypertrace.graphql.label.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;
import org.hypertrace.graphql.label.schema.shared.LabelData;

@GraphQLName(UpdateLabel.TYPE_NAME)
public interface UpdateLabel extends Identifiable {
  String TYPE_NAME = "UpdateLabel";
  String ARGUMENT_NAME = "updateLabel";

  String LABEL_DATA_KEY = "data";

  @GraphQLField()
  @GraphQLName(LABEL_DATA_KEY)
  @GraphQLNonNull()
  LabelData data();
}
