package org.hypertrace.graphql.label.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import javax.annotation.Nullable;

@GraphQLName(TokenExtractionRule.TYPE_NAME)
public interface TokenExtractionRule {
  String TYPE_NAME = "TokenExtractionRule";

  String KEY_NAME = "key";
  String ALIAS_KEY = "alias";
  String REGEX_CAPTURE_KEY = "regexCapture";

  @GraphQLField
  @GraphQLName(KEY_NAME)
  @GraphQLNonNull
  String key();

  @GraphQLField
  @GraphQLName(ALIAS_KEY)
  @Nullable
  String alias();

  @GraphQLField
  @GraphQLName(REGEX_CAPTURE_KEY)
  @Nullable
  String regexCapture();
}
