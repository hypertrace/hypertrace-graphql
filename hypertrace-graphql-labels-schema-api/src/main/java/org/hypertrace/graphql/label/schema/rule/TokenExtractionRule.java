package org.hypertrace.graphql.label.schema.rule;

import graphql.annotations.annotationTypes.GraphQLDescription;
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
  @GraphQLDescription("The attribute key to be used to get value")
  @GraphQLName(KEY_NAME)
  @GraphQLNonNull
  String key();

  @GraphQLField
  @GraphQLDescription("Optionally define and use alias for the attribute key in the expression")
  @GraphQLName(ALIAS_KEY)
  @Nullable
  String alias();

  @GraphQLField
  @GraphQLDescription(
      "Optionally define regex capture rule to extract token value from the attribute value. It should have only one capture group. Ex: \"(.*)\\..*\"")
  @GraphQLName(REGEX_CAPTURE_KEY)
  @Nullable
  String regexCapture();
}
