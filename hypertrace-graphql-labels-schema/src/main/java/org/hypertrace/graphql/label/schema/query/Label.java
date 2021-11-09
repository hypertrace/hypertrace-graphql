package org.hypertrace.graphql.label.schema.query;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import javax.annotation.Nullable;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;
import org.hypertrace.graphql.label.schema.shared.LabelData;

@GraphQLName(Label.TYPE_NAME)
public interface Label extends Identifiable {
  String TYPE_NAME = "Label";

  String LABEL_DATA_KEY = "data";
  String CREATED_BY_RULE_ID_KEY = "createdByRuleId";

  @GraphQLField()
  @GraphQLName(LABEL_DATA_KEY)
  @GraphQLNonNull()
  LabelData data();

  @GraphQLField()
  @GraphQLName(CREATED_BY_RULE_ID_KEY)
  @Nullable
  String createdByRuleId();
}
