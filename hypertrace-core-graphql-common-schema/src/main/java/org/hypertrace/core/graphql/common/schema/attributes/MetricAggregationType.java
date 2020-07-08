package org.hypertrace.core.graphql.common.schema.attributes;

import graphql.annotations.annotationTypes.GraphQLName;

@GraphQLName(MetricAggregationType.TYPE_NAME)
public enum MetricAggregationType {
  COUNT,
  AVG,
  SUM,
  MIN,
  MAX,
  AVGRATE,
  PERCENTILE,
  DISTINCTCOUNT;

  public static final String TYPE_NAME = "MetricAggregationType";
}
