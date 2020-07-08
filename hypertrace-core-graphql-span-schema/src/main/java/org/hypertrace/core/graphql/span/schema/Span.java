package org.hypertrace.core.graphql.span.schema;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeQueryable;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(Span.TYPE_NAME)
public interface Span extends AttributeQueryable, Identifiable {
  String TYPE_NAME = "Span";
}
