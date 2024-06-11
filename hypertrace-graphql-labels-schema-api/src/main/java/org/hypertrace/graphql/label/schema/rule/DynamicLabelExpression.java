package org.hypertrace.graphql.label.schema.rule;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;

@GraphQLName(DynamicLabelExpression.TYPE_NAME)
public interface DynamicLabelExpression {
  String TYPE_NAME = "DynamicLabelExpression";

  String LABEL_EXPRESSION_KEY = "labelExpression";
  String TOKEN_EXTRACTION_RULES_KEY = "tokenExtractionRules";

  @GraphQLField
  @GraphQLName(LABEL_EXPRESSION_KEY)
  @GraphQLNonNull
  String labelExpression();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(TOKEN_EXTRACTION_RULES_KEY)
  List<TokenExtractionRule> tokenExtractionRules();
}
