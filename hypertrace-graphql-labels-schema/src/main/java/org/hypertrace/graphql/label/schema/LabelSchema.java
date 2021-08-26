package org.hypertrace.graphql.label.schema;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.label.fetcher.LabelFetcher;

public interface LabelSchema {
  String LABELS_QUERY_NAME = "labels";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(LABELS_QUERY_NAME)
  @GraphQLDataFetcher(LabelFetcher.class)
  LabelResultSet labels();
}
