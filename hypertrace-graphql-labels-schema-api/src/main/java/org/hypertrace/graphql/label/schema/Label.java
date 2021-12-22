package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(Label.TYPE_NAME)
public interface Label extends Identifiable, LabelData, LabeledEntities {
  String TYPE_NAME = "Label";
  String ARGUMENT_NAME = "label";
}
