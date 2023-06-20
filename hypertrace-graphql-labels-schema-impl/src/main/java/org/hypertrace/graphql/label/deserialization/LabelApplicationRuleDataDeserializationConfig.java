package org.hypertrace.graphql.label.deserialization;

import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.label.schema.rule.LabelApplicationRuleData;

public class LabelApplicationRuleDataDeserializationConfig
    implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return LabelApplicationRuleData.ARGUMENT_NAME;
  }

  @Override
  public Class<?> getArgumentSchema() {
    return LabelApplicationRuleData.class;
  }
}
