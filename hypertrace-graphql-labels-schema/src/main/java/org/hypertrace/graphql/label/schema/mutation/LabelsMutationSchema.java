package org.hypertrace.graphql.label.schema.mutation;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.graphql.label.deserialization.LabelIdArgument;
import org.hypertrace.graphql.label.deserialization.LabelKeyArgument;
import org.hypertrace.graphql.label.mutator.LabelCreateMutator;
import org.hypertrace.graphql.label.mutator.LabelDeleteMutator;
import org.hypertrace.graphql.label.mutator.LabelUpdateMutator;
import org.hypertrace.graphql.label.schema.Label;

public interface LabelsMutationSchema {
  String CREATE_LABEL = "createLabel";
  String DELETE_LABEL = "deleteLabel";
  String UPDATE_LABEL = "updateLabel";
  String LABEL_ARGUMENT_NAME = "newLabel";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(CREATE_LABEL)
  @GraphQLDataFetcher(LabelCreateMutator.class)
  Label createLabel(@GraphQLNonNull @GraphQLName(LabelKeyArgument.ARGUMENT_NAME) String key);

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(DELETE_LABEL)
  @GraphQLDataFetcher(LabelDeleteMutator.class)
  Boolean deleteLabel(@GraphQLNonNull @GraphQLName(LabelIdArgument.ARGUMENT_NAME) String id);

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(UPDATE_LABEL)
  @GraphQLDataFetcher(LabelUpdateMutator.class)
  Label updateLabel(@GraphQLNonNull @GraphQLName(LABEL_ARGUMENT_NAME) Label newLabel);
}
