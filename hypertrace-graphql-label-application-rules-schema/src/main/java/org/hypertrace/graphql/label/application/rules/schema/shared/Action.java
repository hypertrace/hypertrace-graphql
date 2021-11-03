package org.hypertrace.graphql.label.application.rules.schema.shared;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import javax.annotation.Nullable;

@GraphQLName(Action.TYPE_NAME)
public interface Action {
  String TYPE_NAME = "LabelApplicationAction";

  String ENTITY_TYPES_KEY = "entityTypes";
  String OPERATION_KEY = "operation";
  String STATIC_LABELS = "staticLabels";
  String DYNAMIC_LABEL_KEY_KEY = "dynamicLabelKey";
  String VALUE_TYPE_KEY = "actionType";

  @GraphQLName(Operation.TYPE_NAME)
  enum Operation {
    OPERATION_MERGE;
    private static final String TYPE_NAME = "LabelApplicationActionOperator";
  }

  @GraphQLName(LabelApplicationActionType.TYPE_NAME)
  enum LabelApplicationActionType {
    STATIC_LABELS,
    DYNAMIC_LABEL_KEY;
    private static final String TYPE_NAME = "LabelApplicationValueType";
  }

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ENTITY_TYPES_KEY)
  List<String> entityTypes();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(OPERATION_KEY)
  Operation operation();

  @GraphQLField
  @Nullable
  @GraphQLName(STATIC_LABELS)
  StaticLabels staticLabels();

  @GraphQLField
  @Nullable
  @GraphQLName(DYNAMIC_LABEL_KEY_KEY)
  String dynamicLabelKey();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(VALUE_TYPE_KEY)
  LabelApplicationActionType labelApplicationActionType();
}
