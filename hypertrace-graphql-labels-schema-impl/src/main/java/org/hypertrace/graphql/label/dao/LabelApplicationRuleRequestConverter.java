package org.hypertrace.graphql.label.dao;

import static org.hypertrace.graphql.label.schema.rule.StringCondition.StringConditionValueType.VALUE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hypertrace.graphql.label.request.LabelApplicationRuleCreateRequest;
import org.hypertrace.graphql.label.request.LabelApplicationRuleDeleteRequest;
import org.hypertrace.graphql.label.request.LabelApplicationRuleUpdateRequest;
import org.hypertrace.label.application.rule.config.service.v1.CreateLabelApplicationRuleRequest;
import org.hypertrace.label.application.rule.config.service.v1.DeleteLabelApplicationRuleRequest;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Action;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Action.DynamicLabel.TokenExtractionRule;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.CompositeCondition;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Condition;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.LeafCondition;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.StringCondition;
import org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.UnaryCondition;
import org.hypertrace.label.application.rule.config.service.v1.UpdateLabelApplicationRuleRequest;

class LabelApplicationRuleRequestConverter {
  public CreateLabelApplicationRuleRequest convertCreationRequest(
      LabelApplicationRuleCreateRequest labelApplicationRuleCreateRequest) {
    org.hypertrace.graphql.label.schema.rule.LabelApplicationRuleData data =
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
      org.hypertrace.graphql.label.schema.rule.LabelApplicationRuleData data) {
    LabelApplicationRuleData.Builder convertedDataBuilder =
        LabelApplicationRuleData.newBuilder()
            .setName(data.name())
            .setMatchingCondition(convertConditionList(data.conditionList()))
            .setLabelAction(convertLabelAction(data.action()))
            .setEnabled(data.enabled());
    Optional.ofNullable(data.description()).ifPresent(convertedDataBuilder::setDescription);
    return convertedDataBuilder.build();
  }

  private Action.Operation getOperationFromAction(
      org.hypertrace.graphql.label.schema.rule.Action action) {
    switch (action.operation()) {
      case OPERATION_MERGE:
        return Action.Operation.OPERATION_MERGE;
      default:
        throw new IllegalArgumentException("Unsupported Operation");
    }
  }

  private Action convertLabelAction(org.hypertrace.graphql.label.schema.rule.Action action) {
    Action.Operation operation = getOperationFromAction(action);
    Action.Builder actionBuilder =
        Action.newBuilder().setOperation(operation).addAllEntityTypes(action.entityTypes());
    switch (action.type()) {
      case STATIC_LABELS:
        return actionBuilder
            .setStaticLabels(
                Action.StaticLabels.newBuilder().addAllIds(action.staticLabels().ids()).build())
            .build();
      case DYNAMIC_LABEL_KEY:
        return actionBuilder.setDynamicLabelKey(action.dynamicLabelKey()).build();
      case DYNAMIC_LABEL:
        return actionBuilder
            .setDynamicLabelExpression(
                Action.DynamicLabel.newBuilder()
                    .setLabelExpression(action.dynamicLabel().expression())
                    .addAllTokenExtractionRules(
                        convertTokenExtractionRules(action.dynamicLabel().tokenExtractionRules())))
            .build();
      default:
        throw new IllegalArgumentException("Unsupported action value");
    }
  }

  private List<TokenExtractionRule> convertTokenExtractionRules(
      List<org.hypertrace.graphql.label.schema.rule.TokenExtractionRule> tokenExtractionRules) {
    return tokenExtractionRules.stream()
        .map(
            rule -> {
              TokenExtractionRule.Builder builder =
                  TokenExtractionRule.newBuilder().setKey(rule.key());
              Optional.ofNullable(rule.alias()).ifPresent(builder::setAlias);
              Optional.ofNullable(rule.regexCapture()).ifPresent(builder::setRegexCapture);
              return builder.build();
            })
        .collect(Collectors.toList());
  }

  Condition convertConditionList(
      List<org.hypertrace.graphql.label.schema.rule.Condition> conditionList) {
    if (conditionList.size() == 1) {
      return convertCondition(conditionList.get(0));
    } else {
      List<Condition> childConditions =
          conditionList.stream().map(this::convertCondition).collect(Collectors.toList());

      CompositeCondition compositeCondition =
          CompositeCondition.newBuilder()
              .addAllChildren(childConditions)
              .setOperator(CompositeCondition.LogicalOperator.LOGICAL_OPERATOR_AND)
              .build();
      return Condition.newBuilder().setCompositeCondition(compositeCondition).build();
    }
  }

  Condition convertCondition(org.hypertrace.graphql.label.schema.rule.Condition condition) {
    LeafCondition leafCondition = convertLeafCondition(condition.leafCondition());
    return Condition.newBuilder().setLeafCondition(leafCondition).build();
  }

  LeafCondition convertLeafCondition(
      org.hypertrace.graphql.label.schema.rule.LeafCondition leafCondition) {
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
      org.hypertrace.graphql.label.schema.rule.StringCondition stringCondition) {
    StringCondition.Builder stringConditionBuilder =
        StringCondition.newBuilder()
            .setOperator(convertStringConditionOperator(stringCondition.operator()));
    final org.hypertrace.graphql.label.schema.rule.StringCondition.StringConditionValueType type =
        Optional.ofNullable(stringCondition.stringConditionValueType()).orElse(VALUE);
    switch (type) {
      case VALUES:
        return stringConditionBuilder
            .setValues(
                StringCondition.StringList.newBuilder().addAllValues(stringCondition.values()))
            .build();
      case VALUE:
      default:
        return stringConditionBuilder.setValue(stringCondition.value()).build();
    }
  }

  UnaryCondition convertUnaryCondition(
      org.hypertrace.graphql.label.schema.rule.UnaryCondition unaryCondition) {
    return UnaryCondition.newBuilder()
        .setOperator(convertUnaryOperator(unaryCondition.operator()))
        .build();
  }

  StringCondition.Operator convertStringConditionOperator(
      org.hypertrace.graphql.label.schema.rule.StringCondition.Operator operator) {
    switch (operator) {
      case OPERATOR_EQUALS:
        return StringCondition.Operator.OPERATOR_EQUALS;
      case OPERATOR_MATCHES_REGEX:
        return StringCondition.Operator.OPERATOR_MATCHES_REGEX;
      case OPERATOR_MATCHES_IPS:
        return StringCondition.Operator.OPERATOR_MATCHES_IPS;
      case OPERATOR_NOT_MATCHES_IPS:
        return StringCondition.Operator.OPERATOR_NOT_MATCHES_IPS;
      default:
        throw new IllegalArgumentException("Unsupported String Condition Operator");
    }
  }

  UnaryCondition.Operator convertUnaryOperator(
      org.hypertrace.graphql.label.schema.rule.UnaryCondition.Operator operator) {
    switch (operator) {
      case OPERATOR_EXISTS:
        return UnaryCondition.Operator.OPERATOR_EXISTS;
      default:
        throw new IllegalArgumentException("Unsupported Unary Condition Operator");
    }
  }
}
