package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeQueryable;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(LabeledEntity.TYPE_NAME)
public interface LabeledEntity extends AttributeQueryable, Identifiable {
  String TYPE_NAME = "LabeledEntity";
}
