package org.hypertrace.core.graphql.common.deserialization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import javax.inject.Inject;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterType;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;

class FilterArgumentDeserializationConfig implements ArgumentDeserializationConfig {

  private final Class<? extends AttributeScope> attributeScopeImplementationClass;

  @Inject
  FilterArgumentDeserializationConfig(
      Class<? extends AttributeScope> attributeScopeImplementationClass) {
    this.attributeScopeImplementationClass = attributeScopeImplementationClass;
  }

  @Override
  public String getArgumentKey() {
    return FilterArgument.ARGUMENT_NAME;
  }

  @Override
  public Class<FilterArgument> getArgumentSchema() {
    return FilterArgument.class;
  }

  @Override
  public List<Module> jacksonModules() {
    return List.of(
        new SimpleModule()
            .addAbstractTypeMapping(FilterArgument.class, DefaultFilterArgument.class)
            .addAbstractTypeMapping(AttributeScope.class, this.attributeScopeImplementationClass));
  }

  @Value
  @Accessors(fluent = true)
  @NoArgsConstructor(force = true)
  private static class DefaultFilterArgument implements FilterArgument {
    @JsonProperty(FILTER_ARGUMENT_TYPE)
    FilterType type;

    @JsonProperty(FILTER_ARGUMENT_KEY)
    String key;

    @JsonProperty(FILTER_ARGUMENT_OPERATOR)
    FilterOperatorType operator;

    @JsonProperty(FILTER_ARGUMENT_VALUE)
    Object value;

    @JsonProperty(FILTER_ARGUMENT_ID_TYPE)
    AttributeScope idScope;
  }
}
