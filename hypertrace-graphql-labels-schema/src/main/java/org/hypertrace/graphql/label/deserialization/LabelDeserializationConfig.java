package org.hypertrace.graphql.label.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.label.schema.Label;

import java.util.List;

public class LabelDeserializationConfig implements ArgumentDeserializationConfig {

    @Override
    public String getArgumentKey() {
        return Label.ARGUMENT_NAME;
    }

    @Override
    public Class<?> getArgumentSchema() {
        return Label.class;
    }

    @Override
    public List<Module> jacksonModules() {
        return List.of(
                new SimpleModule()
                        .addAbstractTypeMapping(
                                Label.class, LabelArgument.class));
    }

    @Value
    @Accessors(fluent = true)
    @NoArgsConstructor(force = true)
    private static class LabelArgument implements Label {
        @JsonProperty(IDENTITY_FIELD_NAME)
        String id;
        @JsonProperty(KEY)
        String key;
    }
}
