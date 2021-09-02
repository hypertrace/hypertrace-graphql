package org.hypertrace.graphql.label.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.label.schema.mutation.CreateLabel;

public class CreateLabelDeserializationConfig implements ArgumentDeserializationConfig {
  @Override
  public String getArgumentKey() {
    return CreateLabel.ARGUMENT_NAME;
  }

  @Override
  public Class<?> getArgumentSchema() {
    return CreateLabel.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule().addAbstractTypeMapping(CreateLabel.class, CreateLabelArgument.class));
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class CreateLabelArgument implements CreateLabel {
    @JsonProperty(KEY)
    String key;
  }
}
