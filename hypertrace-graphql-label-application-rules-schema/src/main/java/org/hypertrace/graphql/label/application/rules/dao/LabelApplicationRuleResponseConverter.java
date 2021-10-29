package org.hypertrace.graphql.label.application.rules.dao;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.graphql.label.application.rules.schema.query.LabelApplicationRuleResultSet;
import org.hypertrace.graphql.label.application.rules.schema.shared.Action;
import org.hypertrace.graphql.label.application.rules.schema.shared.CompositeCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.Condition;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRule;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRuleData;
import org.hypertrace.graphql.label.application.rules.schema.shared.LeafCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.StaticLabels;
import org.hypertrace.graphql.label.application.rules.schema.shared.StringCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.UnaryCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.ValueCondition;
import org.hypertrace.label.application.rule.config.service.v1.CreateLabelApplicationRuleResponse;
import org.hypertrace.label.application.rule.config.service.v1.GetLabelApplicationRulesResponse;
import org.hypertrace.label.application.rule.config.service.v1.UpdateLabelApplicationRuleResponse;

class LabelApplicationRuleResponseConverter {

  // TODO: Refactor logic into convert methods from the static methods
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

  Single<LabelApplicationRule> convertUpdateLabelApplicationRuleResponse(
      UpdateLabelApplicationRuleResponse response) {
    return convertLabelApplicationRule(response.getLabelApplicationRule());
  }

  Single<Boolean> buildDeleteLabelApplicationRuleResponse() {
    return Single.just(true);
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
      Action action = DefaultAction.of(data.getLabelAction());
      return new DefaultLabelApplicationRuleData(ruleName, matchingCondition, action);
    }

    String name;
    Condition condition;
    Action action;
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultAction implements Action {
    private static Action of(
        org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Action
            action) {
      List<String> entityTypes = action.getEntityTypesList();
      Operation operation;
      switch (action.getOperation()) {
        case OPERATION_MERGE:
          operation = Operation.OPERATION_MERGE;
          break;
        default:
          throw new IllegalArgumentException("Invalid operation in label");
      }
      switch (action.getValueCase()) {
        case STATIC_LABELS:
          return new DefaultAction(
              entityTypes,
              operation,
              DefaultStaticLabels.of(action.getStaticLabels()),
              null,
              ValueType.STATIC_LABELS);
        case DYNAMIC_LABEL_KEY:
          return new DefaultAction(
              entityTypes, operation, null, action.getDynamicLabelKey(), ValueType.STATIC_LABELS);
        default:
          throw new IllegalArgumentException("Unsupported value type in action");
      }
    }

    List<String> entityTypes;
    Operation operation;
    StaticLabels staticLabels;
    String dynamicLabelKey;
    ValueType valueType;
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultStaticLabels implements StaticLabels {
    private static StaticLabels of(
        org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Action
                .StaticLabels
            staticLabels) {
      return new DefaultStaticLabels(staticLabels.getIdsList());
    }

    List<String> ids;
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultCondition implements Condition {
    private static Condition of(
        org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Condition
            condition) {
      switch (condition.getConditionCase()) {
        case LEAF_CONDITION:
          return new DefaultCondition(
              DefaultLeafCondition.of(condition.getLeafCondition()),
              null,
              ConditionType.LEAF_CONDITION);
        case COMPOSITE_CONDITION:
        default:
          throw new IllegalArgumentException("Condition not set correctly");
      }
    }

    LeafCondition leafCondition;
    CompositeCondition compositeCondition;
    ConditionType conditionType;
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
          return new DefaultValueCondition(
              DefaultStringCondition.of(leafCondition.getStringCondition()),
              null,
              ValueConditionType.STRING_CONDITION);
        case UNARY_CONDITION:
          return new DefaultValueCondition(
              null,
              DefaultUnaryCondition.of(leafCondition.getUnaryCondition()),
              ValueConditionType.UNARY_CONDITION);
        case JSON_CONDITION:
        default:
          throw new IllegalArgumentException("Invalid operator in leaf condition");
      }
    }

    StringCondition stringCondition;
    UnaryCondition unaryCondition;
    ValueConditionType valueConditionType;
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
