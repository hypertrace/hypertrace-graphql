package org.hypertrace.graphql.label.application.rules.dao;

import java.util.stream.Collectors;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleCreateRequest;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleDeleteRequest;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleUpdateRequest;
import org.hypertrace.label.application.rule.config.service.v1.CreateLabelApplicationRuleRequest;
import org.hypertrace.label.application.rule.config.service.v1.DeleteLabelApplicationRuleRequest;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Action;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.CompositeCondition;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Condition;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.LeafCondition;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.StringCondition;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.UnaryCondition;
import org.hypertrace.label.application.rule.config.service.v1.UpdateLabelApplicationRuleRequest;

class LabelApplicationRuleRequestConverter {
  public CreateLabelApplicationRuleRequest convertCreationRequest(
      LabelApplicationRuleCreateRequest labelApplicationRuleCreateRequest) {
    org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRuleData data =
        labelApplicationRuleCreateRequest.labelApplicationRuleData();
    return CreateLabelApplicationRuleRequest.newBuilder()
        .setData(convertLabelApplicationRuleData(data))
        .build();
  }

  public UpdateLabelApplicationRuleRequest convertUpdateRequest(
      LabelApplicationRuleUpdateRequest labelApplicationRuleUpdateRequest) {
    return UpdateLabelApplicationRuleRequest.newBuilder()
        .setId(labelApplicationRuleUpdateRequest.labelApplicationRule().id())
        .setData(
            convertLabelApplicationRuleData(
                labelApplicationRuleUpdateRequest
                    .labelApplicationRule()
                    .labelApplicationRuleData()))
        .build();
  }

  public DeleteLabelApplicationRuleRequest convertDeleteRequest(
      LabelApplicationRuleDeleteRequest labelApplicationRuleDeleteRequest) {
    return DeleteLabelApplicationRuleRequest.newBuilder()
        .setId(labelApplicationRuleDeleteRequest.id())
        .build();
  }

  private LabelApplicationRuleData convertLabelApplicationRuleData(
      org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRuleData data) {
    return LabelApplicationRuleData.newBuilder()
        .setName(data.name())
        .setMatchingCondition(convertMatchingCondition(data.condition()))
        .setLabelAction(convertLabelAction(data.action()))
        .build();
  }

  private Condition convertMatchingCondition(
      org.hypertrace.graphql.label.application.rules.schema.shared.Condition condition) {
    switch (condition.conditionType()) {
      case LEAF_CONDITION:
        return Condition.newBuilder()
            .setLeafCondition(convertLeafCondition(condition.leafCondition()))
            .build();
      case COMPOSITE_CONDITION:
        return Condition.newBuilder()
            .setCompositeCondition(convertCompositeCondition(condition.compositeCondition()))
            .build();
      default:
        throw new IllegalArgumentException("Error when parsing matching condition");
    }
  }

  private void setOperationInAction(
      org.hypertrace.graphql.label.application.rules.schema.shared.Action action,
      Action.Builder actionBuilder) {
    switch (action.operation()) {
      case OPERATION_MERGE:
        actionBuilder.setOperation(Action.Operation.OPERATION_MERGE);
        return;
      default:
        throw new IllegalArgumentException("Unsupported Operation");
    }
  }

  private void setLabelsInAction(
      org.hypertrace.graphql.label.application.rules.schema.shared.Action action,
      Action.Builder actionBuilder) {
    switch (action.valueType()) {
      case STATIC_LABELS:
        actionBuilder.setStaticLabels(
            Action.StaticLabels.newBuilder().addAllIds(action.staticLabels().ids()).build());
        return;
      case DYNAMIC_LABEL_KEY:
        actionBuilder.setDynamicLabelKey(action.dynamicLabelKey()).build();
        return;
      default:
        throw new IllegalArgumentException("Unsupported action value");
    }
  }

  private Action convertLabelAction(
      org.hypertrace.graphql.label.application.rules.schema.shared.Action action) {
    Action.Builder actionBuilder = Action.newBuilder().addAllEntityTypes(action.entityTypes());
    setOperationInAction(action, actionBuilder);
    setLabelsInAction(action, actionBuilder);
    return actionBuilder.build();
  }

  CompositeCondition convertCompositeCondition(
      org.hypertrace.graphql.label.application.rules.schema.shared.CompositeCondition
          compositeCondition) {
    CompositeCondition.Builder compositeConditionBuilder =
        CompositeCondition.newBuilder()
            .addAllChildren(
                compositeCondition.children().stream()
                    .map(this::convertLeafCondition)
                    .map(
                        leafCondition ->
                            Condition.newBuilder().setLeafCondition(leafCondition).build())
                    .collect(Collectors.toList()));
    switch (compositeCondition.operator()) {
      case LOGICAL_OPERATOR_AND:
        return compositeConditionBuilder
            .setOperator(CompositeCondition.LogicalOperator.LOGICAL_OPERATOR_AND)
            .build();
      case LOGICAL_OPERATOR_OR:
        return compositeConditionBuilder
            .setOperator(CompositeCondition.LogicalOperator.LOGICAL_OPERATOR_OR)
            .build();
      default:
        throw new IllegalArgumentException("Composite Condition Conversion Failed");
    }
  }

  LeafCondition convertLeafCondition(
      org.hypertrace.graphql.label.application.rules.schema.shared.LeafCondition leafCondition) {
    LeafCondition.Builder leafConditionBuilder =
        LeafCondition.newBuilder()
            .setKeyCondition(convertStringCondition(leafCondition.keyCondition()));
    switch (leafCondition.valueCondition().valueConditionType()) {
      case STRING_CONDITION:
        return leafConditionBuilder
            .setStringCondition(
                convertStringCondition(leafCondition.valueCondition().stringCondition()))
            .build();
      case UNARY_CONDITION:
        return leafConditionBuilder
            .setUnaryCondition(
                convertUnaryCondition(leafCondition.valueCondition().unaryCondition()))
            .build();
      default:
        throw new IllegalArgumentException("Unsupported Leaf Condition");
    }
  }

  StringCondition convertStringCondition(
      org.hypertrace.graphql.label.application.rules.schema.shared.StringCondition
          stringCondition) {
    return StringCondition.newBuilder()
        .setOperator(convertStringConditionOperator(stringCondition.operator()))
        .setValue(stringCondition.value())
        .build();
  }

  UnaryCondition convertUnaryCondition(
      org.hypertrace.graphql.label.application.rules.schema.shared.UnaryCondition unaryCondition) {
    return UnaryCondition.newBuilder()
        .setOperator(convertUnaryOperator(unaryCondition.operator()))
        .build();
  }

  StringCondition.Operator convertStringConditionOperator(
      org.hypertrace.graphql.label.application.rules.schema.shared.StringCondition.Operator
          operator) {
    switch (operator) {
      case OPERATOR_EQUALS:
        return StringCondition.Operator.OPERATOR_EQUALS;
      case OPERATOR_MATCHES_REGEX:
        return StringCondition.Operator.OPERATOR_MATCHES_REGEX;
      default:
        throw new IllegalArgumentException("Unsupported String Condition Operator");
    }
  }

  UnaryCondition.Operator convertUnaryOperator(
      org.hypertrace.graphql.label.application.rules.schema.shared.UnaryCondition.Operator
          operator) {
    switch (operator) {
      case OPERATOR_EXISTS:
        return UnaryCondition.Operator.OPERATOR_EXISTS;
      default:
        throw new IllegalArgumentException("Unsupported Unary Condition Operator");
    }
  }
}
