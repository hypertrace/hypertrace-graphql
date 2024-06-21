package org.hypertrace.core.graphql.trace.schema;

import graphql.annotations.annotationTypes.GraphQLName;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeQueryable;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(Trace.TYPE_NAME)
public interface Trace extends AttributeQueryable, Identifiable {
  String TYPE_NAME = "Trace";
}
