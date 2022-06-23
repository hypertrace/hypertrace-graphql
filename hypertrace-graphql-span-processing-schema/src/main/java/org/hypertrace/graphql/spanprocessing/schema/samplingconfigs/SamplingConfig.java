package org.hypertrace.graphql.spanprocessing.schema.samplingconfigs;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.time.Instant;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(SamplingConfig.TYPE_NAME)
public interface SamplingConfig extends Identifiable, SamplingConfigInfo {
  String TYPE_NAME = "SamplingConfig";

  String CREATION_TIME_KEY = "creationTime";
  String LAST_UPDATED_TIME_KEY = "lastUpdatedTime";

  @GraphQLField
  @GraphQLName(CREATION_TIME_KEY)
  @GraphQLNonNull
  Instant creationTime();

  @GraphQLField
  @GraphQLName(LAST_UPDATED_TIME_KEY)
  @GraphQLNonNull
  Instant lastUpdatedTime();
}
