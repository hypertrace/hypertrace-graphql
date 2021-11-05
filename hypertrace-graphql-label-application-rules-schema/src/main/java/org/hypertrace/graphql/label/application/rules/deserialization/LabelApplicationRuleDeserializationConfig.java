package org.hypertrace.graphql.label.application.rules.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.label.application.rules.schema.shared.Action;
import org.hypertrace.graphql.label.application.rules.schema.shared.Condition;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRule;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRuleData;
import org.hypertrace.graphql.label.application.rules.schema.shared.LeafCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.StaticLabels;
import org.hypertrace.graphql.label.application.rules.schema.shared.StringCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.UnaryCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.ValueCondition;

public class LabelApplicationRuleDeserializationConfig implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return LabelApplicationRule.ARGUMENT_NAME;
  }

  @Override
  public Class<?> getArgumentSchema() {
    return LabelApplicationRule.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(LabelApplicationRule.class, LabelApplicationRuleArgument.class)
            .addAbstractTypeMapping(
                LabelApplicationRuleData.class, LabelApplicationRuleDataArgument.class)
            .addAbstractTypeMapping(Action.class, ActionArgument.class)
            .addAbstractTypeMapping(StaticLabels.class, StaticLabelsArgument.class)
            .addAbstractTypeMapping(Condition.class, ConditionArgument.class)
            .addAbstractTypeMapping(LeafCondition.class, LeafConditionArgument.class)
            .addAbstractTypeMapping(ValueCondition.class, ValueConditionArgument.class)
            .addAbstractTypeMapping(StringCondition.class, StringConditionArgument.class)
            .addAbstractTypeMapping(UnaryCondition.class, UnaryConditionArgument.class));
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class LabelApplicationRuleArgument implements LabelApplicationRule {
    @JsonProperty(IDENTITY_FIELD_NAME)
    String id;

    @JsonProperty(LABEL_APPLICATION_RULE_DATA_KEY)
    LabelApplicationRuleData labelApplicationRuleData;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class LabelApplicationRuleDataArgument implements LabelApplicationRuleData {
    @JsonProperty(NAME_KEY)
    String name;

    @JsonProperty(CONDITION_LIST_KEY)
    List<Condition> conditionList;

    @JsonProperty(ACTION_KEY)
    Action action;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class ActionArgument implements Action {
    @JsonProperty(ENTITY_TYPES_KEY)
    List<String> entityTypes;

    @JsonProperty(OPERATION_KEY)
    Operation operation;

    @JsonProperty(STATIC_LABELS)
    StaticLabels staticLabels;

    @JsonProperty(DYNAMIC_LABEL_KEY_KEY)
    String dynamicLabelKey;

    @JsonProperty(ACTION_TYPE_KEY)
    ActionType type;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class StaticLabelsArgument implements StaticLabels {
    @JsonProperty(IDS_KEY)
    List<String> ids;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class ConditionArgument implements Condition {
    @JsonProperty(LEAF_CONDITION_KEY)
    LeafCondition leafCondition;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class LeafConditionArgument implements LeafCondition {
    @JsonProperty(KEY_CONDITION_KEY)
    StringCondition keyCondition;

    @JsonProperty(VALUE_CONDITION_KEY)
    ValueCondition valueCondition;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class ValueConditionArgument implements ValueCondition {
    @JsonProperty(STRING_CONDITION_KEY)
    StringCondition stringCondition;

    @JsonProperty(UNARY_CONDITION_KEY)
    UnaryCondition unaryCondition;

    @JsonProperty(VALUE_CONDITION_TYPE_KEY)
    ValueConditionType valueConditionType;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class StringConditionArgument implements StringCondition {
    @JsonProperty(OPERATOR_KEY)
    Operator operator;

    @JsonProperty(VALUE_KEY)
    String value;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class UnaryConditionArgument implements UnaryCondition {
    @JsonProperty(OPERATOR_KEY)
    Operator operator;
  }
}
