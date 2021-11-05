package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;

@GraphQLName(StaticLabels.TYPE_NAME)
public interface StaticLabels {
  String TYPE_NAME = "LabelApplicationStaticLabels";

  String IDS_KEY = "ids";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(IDS_KEY)
  List<String> ids();
}
