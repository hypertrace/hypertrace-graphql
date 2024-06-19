package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.time.Instant;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(ExcludeSpanRule.TYPE_NAME)
@GraphQLDescription(
    "An ExcludeSpanRule describes a set of conditions based on span attributes. When matched, a span is discarded and not eligible for further processing.")
public interface ExcludeSpanRule extends Identifiable, ExcludeSpanRuleInfo {
  String TYPE_NAME = "ExcludeSpanRule";

  String CREATION_TIME_KEY = "creationTime";
  String LAST_UPDATED_TIME_KEY = "lastUpdatedTime";

  @GraphQLField
  @GraphQLName(CREATION_TIME_KEY)
  @GraphQLDescription("Exclude span rule creation time")
  @GraphQLNonNull
  Instant creationTime();

  @GraphQLField
  @GraphQLName(LAST_UPDATED_TIME_KEY)
  @GraphQLDescription("Exclude span rule last update time")
  @GraphQLNonNull
  Instant lastUpdatedTime();
}
