package org.hypertrace.graphql.label.application.rules.dao;

import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleCreateRequest;
import org.hypertrace.graphql.label.application.rules.schema.shared.Condition;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRuleData;
import org.hypertrace.graphql.label.application.rules.schema.shared.LeafCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.StringCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.UnaryCondition;
import org.hypertrace.label.application.rule.config.service.v1.CreateLabelApplicationRuleRequest;

public class LabelApplicationRuleRequestConverter {
  CreateLabelApplicationRuleRequest convertCreationRequest(
      LabelApplicationRuleCreateRequest labelApplicationRuleCreateRequest) {
    LabelApplicationRuleData data =
        labelApplicationRuleCreateRequest
            .createLabelApplicationRuleRequest()
            .labelApplicationRuleData();
    return CreateLabelApplicationRuleRequest.newBuilder()
        .setData(
            org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
                .newBuilder()
                .setName(data.name())
                .setMatchingCondition(convertMatchingCondition(data.condition()))
                .setLabelAction(convertLabelAction()))
        .build();
  }

  org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Condition
      convertMatchingCondition(Condition condition) {
    switch (condition.conditionType()) {
      case LEAF_CONDITION:
        return org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
            .Condition.newBuilder()
            .setLeafCondition(convertLeafCondition(condition.leafCondition()))
            .build();
      case COMPOSITE_CONDITION:
      default:
        throw new IllegalArgumentException("Error when parsing matching condition");
    }
  }

  org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Action
      convertLabelAction() {
    return null;
  }

  org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.LeafCondition
      convertLeafCondition(LeafCondition leafCondition) {
    org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.LeafCondition
            .Builder
        leafConditionBuilder =
            org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
                .LeafCondition.newBuilder()
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

  org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.StringCondition
      convertStringCondition(StringCondition stringCondition) {
    return org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
        .StringCondition.newBuilder()
        .setOperator(convertStringConditionOperator(stringCondition.operator()))
        .setValue(stringCondition.value())
        .build();
  }

  org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.UnaryCondition
      convertUnaryCondition(UnaryCondition unaryCondition) {
    return org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
        .UnaryCondition.newBuilder()
        .setOperator(convertUnaryOperator(unaryCondition.operator()))
        .build();
  }

  org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.StringCondition
          .Operator
      convertStringConditionOperator(StringCondition.Operator operator) {
    switch (operator) {
      case OPERATOR_EQUALS:
        return org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
            .StringCondition.Operator.OPERATOR_EQUALS;
      case OPERATOR_MATCHES_REGEX:
        return org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
            .StringCondition.Operator.OPERATOR_MATCHES_REGEX;
      default:
        throw new IllegalArgumentException("Unsupported String Condition Operator");
    }
  }

  org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.UnaryCondition
          .Operator
      convertUnaryOperator(UnaryCondition.Operator operator) {
    switch (operator) {
      case OPERATOR_EXISTS:
        return org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
            .UnaryCondition.Operator.OPERATOR_EXISTS;
      default:
        throw new IllegalArgumentException("Unsupported Unary Condition Operator");
    }
  }
}
