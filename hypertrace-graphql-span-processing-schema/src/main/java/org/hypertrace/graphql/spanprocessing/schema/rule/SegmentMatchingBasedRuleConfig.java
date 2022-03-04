package org.hypertrace.graphql.spanprocessing.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;

@GraphQLName(SegmentMatchingBasedRuleConfig.TYPE_NAME)
public interface SegmentMatchingBasedRuleConfig {
  String TYPE_NAME = "SegmentMatchingBasedRuleConfig";

  String REGEXES_KEY = "regexes";
  String VALUES_KEY = "values";

  @GraphQLField
  @GraphQLName(REGEXES_KEY)
  @GraphQLNonNull
  List<String> regexes();

  @GraphQLField
  @GraphQLName(VALUES_KEY)
  @GraphQLNonNull
  List<String> values();
}
