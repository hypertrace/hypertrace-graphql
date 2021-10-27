package org.hypertrace.graphql.label.application.rules.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.label.application.rules.schema.mutation.CreateLabelApplicationRule;
import org.hypertrace.graphql.label.application.rules.schema.shared.CompositeCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.Condition;
import org.hypertrace.graphql.label.application.rules.schema.shared.LabelApplicationRuleData;
import org.hypertrace.graphql.label.application.rules.schema.shared.LeafCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.StringCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.UnaryCondition;
import org.hypertrace.graphql.label.application.rules.schema.shared.ValueCondition;

public class CreateLabelApplicationRuleDeserializationConfig
    implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return CreateLabelApplicationRule.ARGUMENT_NAME;
  }

  @Override
  public Class<?> getArgumentSchema() {
    return CreateLabelApplicationRule.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(
                CreateLabelApplicationRule.class, CreateLabelApplicationRuleArgument.class)
            .addAbstractTypeMapping(
                LabelApplicationRuleData.class, LabelApplicationRuleDataArgument.class)
            .addAbstractTypeMapping(Condition.class, ConditionArgument.class)
            .addAbstractTypeMapping(LeafCondition.class, LeafConditionArgument.class)
            .addAbstractTypeMapping(CompositeCondition.class, CompositeConditionArgument.class)
            .addAbstractTypeMapping(StringCondition.class, StringConditionArgument.class)
            .addAbstractTypeMapping(UnaryCondition.class, UnaryConditionArgument.class));
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class CreateLabelApplicationRuleArgument implements CreateLabelApplicationRule {
    @JsonProperty(LABEL_APPLICATION_RULE_DATA)
    LabelApplicationRuleData labelApplicationRuleData;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class LabelApplicationRuleDataArgument implements LabelApplicationRuleData {
    @JsonProperty(NAME_KEY)
    String name;

    @JsonProperty(CONDITION_KEY)
    Condition condition;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
  @JsonSubTypes({
    @Type(value = LeafConditionArgument.class, name = "LeafConditionArgument"),
    @Type(value = CompositeConditionArgument.class, name = "CompositeConditionArgument")
  })
  private static class ConditionArgument implements Condition {}

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  @JsonTypeName("LeafConditionArgument")
  private static class LeafConditionArgument implements LeafCondition {
    @JsonProperty(KEY_CONDITION_KEY)
    StringCondition keyCondition;

    @JsonProperty(VALUE_CONDITION_KEY)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
      @Type(value = StringConditionArgument.class, name = "StringConditionArgument"),
      @Type(value = UnaryConditionArgument.class, name = "UnaryConditionArgument")
    })
    ValueCondition valueCondition;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  @JsonTypeName("CompositeConditionArgument")
  private static class CompositeConditionArgument implements CompositeCondition {
    @JsonProperty(LOGICAL_OPERATOR_KEY)
    LogicalOperator operator;

    @JsonProperty(CHILDREN_KEY)
    List<LeafCondition> children;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  @JsonTypeName("StringConditionArgument")
  private static class StringConditionArgument implements StringCondition {
    @JsonProperty(OPERATOR_KEY)
    Operator operator;

    @JsonProperty(VALUE_KEY)
    String value;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  @JsonTypeName("UnaryConditionArgument")
  private static class UnaryConditionArgument implements UnaryCondition {
    @JsonProperty(OPERATOR_KEY)
    Operator operator;
  }
}
