package org.hypertrace.graphql.label.schema.rule;

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
  @GraphQLName(EXPRESSION_KEY)
  @GraphQLNonNull
  String expression();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(TOKEN_EXTRACTION_RULES_KEY)
  List<TokenExtractionRule> tokenExtractionRules();
}
