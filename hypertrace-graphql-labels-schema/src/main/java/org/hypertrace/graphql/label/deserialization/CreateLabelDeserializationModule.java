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
import org.hypertrace.graphql.label.schema.shared.LabelData;

public class CreateLabelDeserializationModule implements ArgumentDeserializationConfig {
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
        new SimpleModule()
            .addAbstractTypeMapping(CreateLabel.class, CreateLabelArgument.class)
            .addAbstractTypeMapping(LabelData.class, LabelDataArgument.class));
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class CreateLabelArgument implements CreateLabel {
    @JsonProperty(CREATED_BY_RULE_ID_KEY)
    String createdByRuleId;

    @JsonProperty(LABEL_DATA_KEY)
    LabelData data;
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class LabelDataArgument implements LabelData {
    @JsonProperty(LABEL_NAME_KEY)
    String key;

    @JsonProperty(COLOR_KEY)
    String color;

    @JsonProperty(DESCRIPTION_KEY)
    String description;
  }
}
