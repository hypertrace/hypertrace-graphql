package org.hypertrace.graphql.explorer.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.graphql.explorer.schema.argument.EntityContextOptions;

public class EntityContextOptionsDeserializationConfig implements ArgumentDeserializationConfig {

  @Override
  public String getArgumentKey() {
    return EntityContextOptions.ARGUMENT_NAME;
  }

  @Override
  public Class<EntityContextOptions> getArgumentSchema() {
    return EntityContextOptions.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(
                EntityContextOptions.class,
                EntityContextOptionsDeserializationConfig.DefaultEntityContextOptionsArgument
                    .class));
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class DefaultEntityContextOptionsArgument implements EntityContextOptions {

    @JsonProperty(EntityContextOptions.INCLUDE_NON_LIVE_ENTITIES)
    boolean includeNonLiveEntities;
  }
}
