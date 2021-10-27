package org.hypertrace.graphql.label.application.rules.deserialization;

import com.fasterxml.jackson.databind.Module;
import java.util.List;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;

public class LabelApplicationRuleDeserializationConfig implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return null;
  }

  @Override
  public String getListArgumentKey() {
    return ArgumentDeserializationConfig.super.getListArgumentKey();
  }

  @Override
  public Class<?> getArgumentSchema() {
    return null;
  }

  @Override
  public List<Module> jacksonModules() {
    return ArgumentDeserializationConfig.super.jacksonModules();
  }
}
