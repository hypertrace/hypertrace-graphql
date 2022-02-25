package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.time.Instant;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;

@GraphQLName(ExcludeSpanRule.TYPE_NAME)
public interface ExcludeSpanRule extends Identifiable, ExcludeSpanRuleInfo {
  String TYPE_NAME = "ExcludeSpanRule";

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
