package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.time.Instant;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(ExcludeSpanRule.TYPE_NAME)
@GraphQLDescription(
    "Exclude span rules are the set of rules created using filters on span attributes based on which we exclude spans at source from reaching the platform")
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
