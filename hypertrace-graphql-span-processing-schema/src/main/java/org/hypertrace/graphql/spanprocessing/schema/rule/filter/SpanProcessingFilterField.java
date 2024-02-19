package org.hypertrace.graphql.spanprocessing.schema.rule.filter;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(SpanProcessingFilterField.TYPE_NAME)
public enum SpanProcessingFilterField {
  URL,
  URL_PATH,
  SERVICE_NAME,
  ENVIRONMENT_NAME;
  static final String TYPE_NAME = "SpanProcessingFilterField";
}
