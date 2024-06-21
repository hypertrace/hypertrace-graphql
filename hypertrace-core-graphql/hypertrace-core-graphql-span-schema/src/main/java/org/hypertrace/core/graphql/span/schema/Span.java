package org.hypertrace.core.graphql.span.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeQueryable;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;
import org.hypertrace.core.graphql.log.event.schema.LogEventResultSet;

@GraphQLName(Span.TYPE_NAME)
public interface Span extends AttributeQueryable, Identifiable {
  String TYPE_NAME = "Span";
  String LOG_EVENT_KEY = "logEvents";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LOG_EVENT_KEY)
  LogEventResultSet logEvents();
}
