package org.hypertrace.core.graphql.span.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(ExportSpanResult.TYPE_NAME)
public interface ExportSpanResult {
  String TYPE_NAME = "ExportSpanResult";
  String RESULTS_NAME = "result";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(RESULTS_NAME)
  String result();
}
