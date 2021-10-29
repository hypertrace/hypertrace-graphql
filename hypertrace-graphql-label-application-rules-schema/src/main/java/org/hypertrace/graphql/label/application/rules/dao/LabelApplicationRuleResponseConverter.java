package org.hypertrace.graphql.label.application.rules.dao;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
class LabelApplicationRuleResponseConverter {
  Single<LabelApplicationRule> convertCreateLabelApplicationRuleResponse(
      CreateLabelApplicationRuleResponse response) {
    Optional<LabelApplicationRule> rule =
        convertLabelApplicationRule(response.getLabelApplicationRule());
    if (rule.isPresent()) {
      return Single.just(rule.get());
    } else {
      return Single.error(new IllegalArgumentException("Unable to convert rule create response"));
    }
  }

  Single<LabelApplicationRuleResultSet> convertGetLabelApplicationsRuleResponse(
      GetLabelApplicationRulesResponse response) {
    return Observable.fromIterable(response.getLabelApplicationRulesList())
        .map(this::convertLabelApplicationRule)
        .flatMapSingle(
            convertedRule -> {
              if (convertedRule.isEmpty()) {
                return Single.error(
                    new IllegalArgumentException("Unable to convert a rule in get all response"));
              }
              return Single.just(convertedRule.get());
            })
        .toList()
        .map(ConvertedLabelApplicationRuleResultSet::forRuleList);
  }

  Single<LabelApplicationRule> convertUpdateLabelApplicationRuleResponse(
      UpdateLabelApplicationRuleResponse response) {
    Optional<LabelApplicationRule> rule =
        convertLabelApplicationRule(response.getLabelApplicationRule());
    if (rule.isPresent()) {
      return Single.just(rule.get());
    } else {
      return Single.error(new IllegalArgumentException("Unable to convert rule update response"));
    }
  }

  Single<Boolean> buildDeleteLabelApplicationRuleResponse() {
    return Single.just(true);
  }

  private Optional<LabelApplicationRule> convertLabelApplicationRule(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRule rule) {
    Optional<LabelApplicationRuleData> ruleData = convertLabelApplicationRuleData(rule.getData());
    return ruleData.map(data -> new ConvertedLabelApplicationRule(rule.getId(), data));
  }

  private Optional<LabelApplicationRuleData> convertLabelApplicationRuleData(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData data) {
    Optional<Action> action = convertAction(data.getLabelAction());
    Optional<Condition> condition = convertCondition(data.getMatchingCondition());
    if (condition.isEmpty() || action.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(
        new ConvertedLabelApplicationRuleData(data.getName(), condition.get(), action.get()));
  }

  private Optional<Action.Operation> convertOperationInAction(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Action
          action) {
    switch (action.getOperation()) {
      case OPERATION_MERGE:
        return Optional.of(Action.Operation.OPERATION_MERGE);
      default:
        log.error("Unrecognized Operation type in Action{}", action.getOperation().name());
        return Optional.empty();
    }
  }

  private Optional<Action> convertAction(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Action
          action) {
    List<String> entityTypes = action.getEntityTypesList();
    Optional<Action.Operation> operation = convertOperationInAction(action);
    if (operation.isEmpty()) {
      return Optional.empty();
    }
    switch (action.getValueCase()) {
      case STATIC_LABELS:
        StaticLabels staticLabels = convertStaticLabels(action.getStaticLabels());
        return Optional.of(
            new ConvertedAction(
                entityTypes, operation.get(), staticLabels, null, Action.ValueType.STATIC_LABELS));
      case DYNAMIC_LABEL_KEY:
        return Optional.of(
            new ConvertedAction(
                entityTypes,
                operation.get(),
                null,
                action.getDynamicLabelKey(),
                Action.ValueType.STATIC_LABELS));
      default:
        log.error("Unrecognized Value type in Action {}", action.getValueCase().name());
        return Optional.empty();
    }
  }

  private StaticLabels convertStaticLabels(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Action
              .StaticLabels
          staticLabels) {
    return new ConvertedStaticLabels(staticLabels.getIdsList());
  }

  private Optional<Condition> convertCondition(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Condition
          condition) {
    switch (condition.getConditionCase()) {
      case LEAF_CONDITION:
        Optional<LeafCondition> leafCondition = convertLeafCondition(condition.getLeafCondition());
        return leafCondition.map(
            leafCond ->
                new ConvertedCondition(leafCond, null, Condition.ConditionType.LEAF_CONDITION));
      case COMPOSITE_CONDITION:
        Optional<CompositeCondition> compositeCondition =
            convertCompositeCondition(condition.getCompositeCondition());
        return compositeCondition.map(
            compositeCond ->
                new ConvertedCondition(
                    null, compositeCond, Condition.ConditionType.COMPOSITE_CONDITION));
      default:
        log.error("Unrecognized Condition Type {}", condition.getConditionCase().name());
        return Optional.empty();
    }
  }

  private Optional<CompositeCondition> convertCompositeCondition(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
              .CompositeCondition
          compositeCondition) {
    List<LeafCondition> leafConditionList =
        compositeCondition.getChildrenList().stream()
            .filter(this::isLeafCondition)
            .map(condition -> convertLeafCondition(condition.getLeafCondition()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    if (leafConditionList.size() != compositeCondition.getChildrenList().size()) {
      return Optional.empty();
    }
    Optional<CompositeCondition.LogicalOperator> logicalOperator =
        convertLogicalOperator(compositeCondition);
    return logicalOperator.map(
        operator -> new ConvertedCompositeCondition(operator, leafConditionList));
  }

  private boolean isLeafCondition(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Condition
          condition) {
    switch (condition.getConditionCase()) {
      case LEAF_CONDITION:
        return true;
      case COMPOSITE_CONDITION:
      default:
        return false;
    }
  }

  private Optional<CompositeCondition.LogicalOperator> convertLogicalOperator(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
              .CompositeCondition
          compositeCondition) {
    switch (compositeCondition.getOperator()) {
      case LOGICAL_OPERATOR_AND:
        return Optional.of(CompositeCondition.LogicalOperator.LOGICAL_OPERATOR_AND);
      case LOGICAL_OPERATOR_OR:
        return Optional.of(CompositeCondition.LogicalOperator.LOGICAL_OPERATOR_OR);
      default:
        log.error("Unrecognized Logical Operator in Composite Condition");
        return Optional.empty();
    }
  }

  private Optional<LeafCondition> convertLeafCondition(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.LeafCondition
          leafCondition) {
    Optional<StringCondition> keyCondition =
        convertStringCondition(leafCondition.getKeyCondition());
    Optional<ValueCondition> valueCondition = convertValueCondition(leafCondition);
    if (keyCondition.isEmpty() || valueCondition.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(new ConvertedLeafCondition(keyCondition.get(), valueCondition.get()));
  }

  private Optional<ValueCondition> convertValueCondition(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.LeafCondition
          leafCondition) {
    switch (leafCondition.getConditionCase()) {
      case STRING_CONDITION:
        Optional<StringCondition> stringValueCondition =
            convertStringCondition(leafCondition.getStringCondition());
        return stringValueCondition.map(
            stringCondition ->
                new ConvertedValueCondition(
                    stringCondition, null, ValueCondition.ValueConditionType.STRING_CONDITION));
      case UNARY_CONDITION:
        Optional<UnaryCondition> unaryValueCondition =
            convertUnaryCondition(leafCondition.getUnaryCondition());
        return unaryValueCondition.map(
            unaryCondition ->
                new ConvertedValueCondition(
                    null, unaryCondition, ValueCondition.ValueConditionType.UNARY_CONDITION));
      case JSON_CONDITION:
      default:
        log.error("Unrecognized Value Condition Type {}", leafCondition.getConditionCase().name());
        return Optional.empty();
    }
  }

  private Optional<StringCondition> convertStringCondition(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
              .StringCondition
          stringCondition) {
    Optional<StringCondition.Operator> operator = convertOperatorInStringCondition(stringCondition);
    return operator.map(op -> new ConvertedStringCondition(op, stringCondition.getValue()));
  }

  private Optional<StringCondition.Operator> convertOperatorInStringCondition(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
              .StringCondition
          stringCondition) {
    switch (stringCondition.getOperator()) {
      case OPERATOR_EQUALS:
        return Optional.of(StringCondition.Operator.OPERATOR_EQUALS);
      case OPERATOR_MATCHES_REGEX:
        return Optional.of(StringCondition.Operator.OPERATOR_MATCHES_REGEX);
      default:
        log.error(
            "Unrecognized Operator Type in String Condition {}",
            stringCondition.getOperator().name());
        return Optional.empty();
    }
  }

  private Optional<UnaryCondition> convertUnaryCondition(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
              .UnaryCondition
          unaryCondition) {
    switch (unaryCondition.getOperator()) {
      case OPERATOR_EXISTS:
        return Optional.of(new ConvertedUnaryCondition(UnaryCondition.Operator.OPERATOR_EXISTS));
      default:
        log.error(
            "Unrecognized Operator Type in Unary Condition {}",
            unaryCondition.getOperator().name());
        return Optional.empty();
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedLabelApplicationRule implements LabelApplicationRule {
    String id;
    LabelApplicationRuleData labelApplicationRuleData;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedLabelApplicationRuleData implements LabelApplicationRuleData {
    String name;
    Condition condition;
    Action action;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedAction implements Action {
    List<String> entityTypes;
    Operation operation;
    StaticLabels staticLabels;
    String dynamicLabelKey;
    ValueType valueType;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedStaticLabels implements StaticLabels {
    List<String> ids;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedCondition implements Condition {
    LeafCondition leafCondition;
    CompositeCondition compositeCondition;
    ConditionType conditionType;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedCompositeCondition implements CompositeCondition {
    LogicalOperator operator;
    List<LeafCondition> children;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedLeafCondition implements LeafCondition {
    StringCondition keyCondition;
    ValueCondition valueCondition;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedValueCondition implements ValueCondition {
    StringCondition stringCondition;
    UnaryCondition unaryCondition;
    ValueConditionType valueConditionType;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedUnaryCondition implements UnaryCondition {
    Operator operator;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedStringCondition implements StringCondition {
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
