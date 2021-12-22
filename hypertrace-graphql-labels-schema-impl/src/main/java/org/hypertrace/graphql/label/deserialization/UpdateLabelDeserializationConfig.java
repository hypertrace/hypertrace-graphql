package org.hypertrace.graphql.label.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.label.schema.mutation.UpdateLabel;

public class UpdateLabelDeserializationConfig implements ArgumentDeserializationConfig {

  @Override
  public String getArgumentKey() {
    return UpdateLabel.ARGUMENT_NAME;
  }

  @Override
  public Class<?> getArgumentSchema() {
    return UpdateLabel.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule().addAbstractTypeMapping(UpdateLabel.class, UpdateLabelArgument.class));
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class UpdateLabelArgument implements UpdateLabel {
    @JsonProperty(IDENTITY_FIELD_NAME)
    String id;

    @JsonProperty(KEY)
    String key;

    @JsonProperty(COLOR_KEY)
    String color;

    @JsonProperty(DESCRIPTION_KEY)
    String description;
  }
}
