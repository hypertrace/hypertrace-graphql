package org.hypertrace.graphql.label.application.rules.dao;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.hypertrace.graphql.label.application.rules.schema.query.LabelApplicationRuleResultSet;
import org.hypertrace.graphql.label.application.rules.schema.shared.Action;
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
    Optional<LabelApplicationRule> labelApplicationRule =
        convertLabelApplicationRule(response.getLabelApplicationRule());
    return labelApplicationRule
        .map(Single::just)
        .orElse(
            Single.error(new IllegalArgumentException("Unable to convert rule create response")));
  }

  Single<LabelApplicationRuleResultSet> convertGetLabelApplicationsRuleResponse(
      GetLabelApplicationRulesResponse response) {
    return Observable.fromIterable(response.getLabelApplicationRulesList())
        .map(this::convertLabelApplicationRule)
        .flatMapMaybe(Maybe::fromOptional)
        .toList()
        .map(ConvertedLabelApplicationRuleResultSet::forRuleList);
  }

  Single<LabelApplicationRule> convertUpdateLabelApplicationRuleResponse(
      UpdateLabelApplicationRuleResponse response) {
    Optional<LabelApplicationRule> labelApplicationRule =
        convertLabelApplicationRule(response.getLabelApplicationRule());
    return labelApplicationRule
        .map(Single::just)
        .orElse(
            Single.error(new IllegalArgumentException("Unable to convert rule update response")));
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
    List<Condition> conditionList = convertCondition(data.getMatchingCondition());
    if (conditionList.isEmpty()) {
      return Optional.empty();
    }
    return action.map(
        labelAction ->
            new ConvertedLabelApplicationRuleData(data.getName(), conditionList, labelAction));
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
    switch (action.getValueCase()) {
      case STATIC_LABELS:
        StaticLabels staticLabels = convertStaticLabels(action.getStaticLabels());
        return operation.map(
            op ->
                new ConvertedAction(
                    entityTypes, op, staticLabels, null, Action.ValueType.STATIC_LABELS));
      case DYNAMIC_LABEL_KEY:
        return operation.map(
            op ->
                new ConvertedAction(
                    entityTypes,
                    op,
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

  private List<Condition> convertCondition(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.Condition
          condition) {
    switch (condition.getConditionCase()) {
      case LEAF_CONDITION:
        Optional<Condition> convertedCondition =
            convertLeafCondition(condition.getLeafCondition()).map(ConvertedCondition::new);
        return convertedCondition.map(List::of).orElse(Collections.emptyList());
      case COMPOSITE_CONDITION:
        return convertCompositeCondition(condition.getCompositeCondition());
      default:
        log.error("Unrecognized Condition Type {}", condition.getConditionCase().name());
        return Collections.emptyList();
    }
  }

  private List<Condition> convertCompositeCondition(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData
              .CompositeCondition
          compositeCondition) {
    List<Condition> leafConditionList =
        compositeCondition.getChildrenList().stream()
            .filter(this::isLeafCondition)
            .map(condition -> convertLeafCondition(condition.getLeafCondition()))
            .flatMap(Optional::stream)
            .map(ConvertedCondition::new)
            .collect(Collectors.toList());
    if (leafConditionList.size() != compositeCondition.getChildrenList().size()) {
      return Collections.emptyList();
    }
    return leafConditionList;
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

  private Optional<LeafCondition> convertLeafCondition(
      org.hypertrace.label.application.rule.config.service.v1.LabelApplicationRuleData.LeafCondition
          leafCondition) {
    Optional<StringCondition> keyCondition =
        convertStringCondition(leafCondition.getKeyCondition());
    Optional<ValueCondition> valueCondition = convertValueCondition(leafCondition);
    if (keyCondition.isEmpty() || valueCondition.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(
        new ConvertedLeafCondition(keyCondition.orElseThrow(), valueCondition.orElseThrow()));
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
    List<Condition> conditionList;
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
