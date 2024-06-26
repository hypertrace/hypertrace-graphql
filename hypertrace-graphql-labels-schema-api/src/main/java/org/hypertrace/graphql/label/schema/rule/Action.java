package org.hypertrace.graphql.label.schema.rule;

import graphql.annotations.annotationTypes.GraphQLDescription;
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
  String DYNAMIC_LABEL_KEY = "dynamicLabel";
  String ACTION_TYPE_KEY = "type";

  @GraphQLName(Operation.TYPE_NAME)
  enum Operation {
    OPERATION_MERGE;
    private static final String TYPE_NAME = "LabelApplicationActionOperator";
  }

  @GraphQLName(ActionType.TYPE_NAME)
  enum ActionType {
    STATIC_LABELS,
    DYNAMIC_LABEL_KEY,
    DYNAMIC_LABEL;
    private static final String TYPE_NAME = "LabelApplicationActionType";
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
  @GraphQLDescription("Definition to generate dynamic labels")
  @Nullable
  @GraphQLName(DYNAMIC_LABEL_KEY)
  DynamicLabel dynamicLabel();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ACTION_TYPE_KEY)
  ActionType type();
}
