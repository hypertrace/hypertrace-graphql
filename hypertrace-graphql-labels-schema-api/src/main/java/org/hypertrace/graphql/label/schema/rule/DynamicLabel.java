package org.hypertrace.graphql.label.schema.rule;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;

@GraphQLName(DynamicLabel.TYPE_NAME)
public interface DynamicLabel {
  String TYPE_NAME = "DynamicLabel";

  String EXPRESSION_KEY = "expression";
  String TOKEN_EXTRACTION_RULES_KEY = "tokenExtractionRules";

  @GraphQLField
  @GraphQLDescription(
      "Expression definition to generate dynamic labels. Ex: \"${token1}_${token2}\"")
  @GraphQLName(EXPRESSION_KEY)
  @GraphQLNonNull
  String expression();

  @GraphQLField
  @GraphQLDescription(
      "Rules to extract token from attribute values. These values will replace tokens in the expression.")
  @GraphQLNonNull
  @GraphQLName(TOKEN_EXTRACTION_RULES_KEY)
  List<TokenExtractionRule> tokenExtractionRules();
}
