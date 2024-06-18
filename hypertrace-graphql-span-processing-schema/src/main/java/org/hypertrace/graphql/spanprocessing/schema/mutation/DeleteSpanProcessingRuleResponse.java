package org.hypertrace.graphql.spanprocessing.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;

@GraphQLName(DeleteSpanProcessingRuleResponse.TYPE_NAME)
public interface DeleteSpanProcessingRuleResponse {
  String TYPE_NAME = "DeleteSpanProcessingRuleResponse";
  String DELETE_SPAN_PROCESSING_RULE_RESPONSE_SUCCESS = "success";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(DELETE_SPAN_PROCESSING_RULE_RESPONSE_SUCCESS)
  @GraphQLDescription("Delete span processing rule is success or not")
  boolean success();
}
