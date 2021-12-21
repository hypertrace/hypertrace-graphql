package org.hypertrace.graphql.label.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;
import org.hypertrace.graphql.label.schema.LabelData;

@GraphQLName(UpdateLabel.TYPE_NAME)
public interface UpdateLabel extends Identifiable, LabelData {
  String TYPE_NAME = "UpdateLabel";
  String ARGUMENT_NAME = "label";
}
