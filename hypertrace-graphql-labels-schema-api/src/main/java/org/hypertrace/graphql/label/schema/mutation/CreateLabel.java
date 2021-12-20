package org.hypertrace.graphql.label.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.graphql.label.schema.LabelData;

@GraphQLName(CreateLabel.TYPE_NAME)
public interface CreateLabel extends LabelData {
  String TYPE_NAME = "CreateLabel";
  String ARGUMENT_NAME = "label";
}
