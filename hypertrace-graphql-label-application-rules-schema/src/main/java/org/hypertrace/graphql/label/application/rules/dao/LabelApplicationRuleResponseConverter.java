package org.hypertrace.graphql.label.application.rules.dao;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.graphql.label.application.rules.schema.query.LabelApplicationRuleResultSet;
import org.hypertrace.graphql.label.application.rules.schema.shared.Condition;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRule;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRuleData;
import org.hypertrace.graphql.label.application.rules.schema.shared.LeafCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.StringCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.UnaryCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.ValueCondition;
import org.hypertrace.label.application.rule.config.service.v1.CreateLabelApplicationRuleResponse;
import org.hypertrace.label.application.rule.config.service.v1.GetLabelApplicationRulesResponse;

class LabelApplicationRuleResponseConverter {

  Single<LabelApplicationRuleResultSet> convertGetLabelApplicationsRuleResponse(
      GetLabelApplicationRulesResponse response) {
    return Observable.fromIterable(response.getLabelApplicationRulesList())
        .flatMapSingle(this::convertLabelApplicationRule)
        .toList()
        .map(ConvertedLabelApplicationRuleResultSet::forRuleList);
  }

  Single<LabelApplicationRule> convertCreateLabelApplicationRuleResponse(
      CreateLabelApplicationRuleResponse response) {
    return convertLabelApplicationRule(response.getLabelApplicationRule());
  }

  Single<LabelApplicationRule> convertLabelApplicationRule(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRule rule) {
    try {
      return Single.just(DefaultLabelApplicationRule.of(rule));
    } catch (Exception exception) {
      return Single.error(exception);
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabelApplicationRule implements LabelApplicationRule {
    private static LabelApplicationRule of(
        org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRule rule) {
      return new DefaultLabelApplicationRule(
          rule.getId(), DefaultLabelApplicationRuleData.of(rule.getData()));
    }

    String id;
    LabelApplicationRuleData labelApplicationRuleData;
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabelApplicationRuleData implements LabelApplicationRuleData {
    private static LabelApplicationRuleData of(
        org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData data) {
      String ruleName = data.getName();
      Condition matchingCondition = DefaultCondition.of(data.getMatchingCondition());
      return new DefaultLabelApplicationRuleData(ruleName, matchingCondition);
    }

    String name;
    Condition condition;
  }

  private static class DefaultCondition implements Condition {
    private static Condition of(
        org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Condition
            condition) {
      switch (condition.getConditionCase()) {
        case LEAF_CONDITION:
          return DefaultLeafCondition.of(condition.getLeafCondition());
        case COMPOSITE_CONDITION:
        default:
          throw new IllegalArgumentException("Condition not set correctly");
      }
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLeafCondition implements LeafCondition {
    private static LeafCondition of(
        org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
                .LeafCondition
            leafCondition) {
      return new DefaultLeafCondition(
          DefaultStringCondition.of(leafCondition.getKeyCondition()),
          DefaultValueCondition.of(leafCondition));
    }

    StringCondition keyCondition;
    ValueCondition valueCondition;
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultValueCondition implements ValueCondition {
    private static ValueCondition of(
        org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
                .LeafCondition
            leafCondition) {
      switch (leafCondition.getConditionCase()) {
        case STRING_CONDITION:
          return DefaultStringCondition.of(leafCondition.getStringCondition());
        case UNARY_CONDITION:
          return DefaultUnaryCondition.of(leafCondition.getUnaryCondition());
        case JSON_CONDITION:
        default:
          throw new IllegalArgumentException("Invalid operator in leaf condition");
      }
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultUnaryCondition implements UnaryCondition {
    private static UnaryCondition of(
        org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
                .UnaryCondition
            unaryCondition) {
      switch (unaryCondition.getOperator()) {
        case OPERATOR_EXISTS:
          return new DefaultUnaryCondition(Operator.OPERATOR_EXISTS);
        case OPERATOR_UNSPECIFIED:
        default:
          throw new IllegalArgumentException("Operator not set in unary condition");
      }
    }

    Operator operator;
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultStringCondition implements StringCondition {
    private static StringCondition of(
        org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
                .StringCondition
            stringCondition) {
      switch (stringCondition.getOperator()) {
        case OPERATOR_EQUALS:
          return new DefaultStringCondition(Operator.OPERATOR_EQUALS, stringCondition.getValue());
        case OPERATOR_MATCHES_REGEX:
          return new DefaultStringCondition(
              Operator.OPERATOR_MATCHES_REGEX, stringCondition.getValue());
        default:
          throw new IllegalArgumentException("String Condition operator is not set");
      }
    }

    Operator operator;
    String value;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedLabelApplicationRuleResultSet
      implements LabelApplicationRuleResultSet {
    private static LabelApplicationRuleResultSet forRuleList(List<LabelApplicationRule> ruleList) {
      return new ConvertedLabelApplicationRuleResultSet(ruleList, ruleList.size(), ruleList.size());
    }

    List<LabelApplicationRule> results;
    long total;
    long count;
  }
}
