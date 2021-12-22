package org.hypertrace.graphql.label.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.label.mutator.LabelCreateMutator;
import org.hypertrace.graphql.label.mutator.LabelUpdateMutator;
import org.hypertrace.graphql.label.schema.Label;

public interface LabelMutationSchema {
  String CREATE_LABEL = "createLabel";
  String UPDATE_LABEL = "updateLabel";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(CREATE_LABEL)
  @GraphQLDataFetcher(LabelCreateMutator.class)
  Label createLabel(@GraphQLNonNull @GraphQLName(CreateLabel.ARGUMENT_NAME) CreateLabel label);

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(UPDATE_LABEL)
  @GraphQLDataFetcher(LabelUpdateMutator.class)
  Label updateLabel(@GraphQLNonNull @GraphQLName(Label.ARGUMENT_NAME) UpdateLabel label);
}
