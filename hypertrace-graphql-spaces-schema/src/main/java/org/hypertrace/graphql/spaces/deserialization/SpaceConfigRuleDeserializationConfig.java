package org.hypertrace.graphql.spaces.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRule;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRuleAttributeValueRule;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRuleType;

public class SpaceConfigRuleDeserializationConfig implements ArgumentDeserializationConfig {

  @Override
  public String getArgumentKey() {
    return SpaceConfigRule.ARGUMENT_NAME;
  }

  @Override
  public Class<SpaceConfigRule> getArgumentSchema() {
    return SpaceConfigRule.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(SpaceConfigRule.class, SpaceConfigRuleArgument.class)
            .addAbstractTypeMapping(
                SpaceConfigRuleAttributeValueRule.class,
                SpaceConfigRuleAttributeValueRuleArgument.class));
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class SpaceConfigRuleArgument implements SpaceConfigRule {
    @JsonProperty(IDENTITY_FIELD_NAME)
    String id;

    @JsonProperty(SPACE_CONFIG_RULE_DEFINITION_TYPE_KEY)
    SpaceConfigRuleType type;

    @JsonProperty(SPACE_CONFIG_RULE_DEFINITION_ATTRIBUTE_VALUE_RULE_KEY)
    SpaceConfigRuleAttributeValueRule attributeValueRule;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class SpaceConfigRuleAttributeValueRuleArgument
      implements SpaceConfigRuleAttributeValueRule {
    @JsonProperty(ATTRIBUTE_VALUE_ATTRIBUTE_SCOPE_KEY)
    String attributeScope;

    @JsonProperty(ATTRIBUTE_VALUE_ATTRIBUTE_KEY_KEY)
    String attributeKey;
  }
}
