package org.hypertrace.graphql.spanprocessing.schema.samplingconfigs;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.time.Duration;

@GraphQLName(WindowedRateLimit.TYPE_NAME)
public interface WindowedRateLimit {
  String TYPE_NAME = "SpanProcessingWindowedRateLimit";

  String QUANTITY_ALLOWED_KEY = "quantityAllowed";
  String WINDOW_DURATION_KEY = "windowDuration";

  @GraphQLField
  @GraphQLName(QUANTITY_ALLOWED_KEY)
  @GraphQLNonNull
  Long quantityAllowed();

  @GraphQLField
  @GraphQLName(WINDOW_DURATION_KEY)
  @GraphQLNonNull
  Duration windowDuration();
}
