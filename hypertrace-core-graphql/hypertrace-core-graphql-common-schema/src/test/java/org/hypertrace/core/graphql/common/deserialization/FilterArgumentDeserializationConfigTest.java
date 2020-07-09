package org.hypertrace.core.graphql.common.deserialization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import java.util.List;
import java.util.Map;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterType;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializationConfig;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.deserialization.GraphQlDeserializationRegistryModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FilterArgumentDeserializationConfigTest {
  private ArgumentDeserializer argumentDeserializer;

  private enum TestAttributeScope implements AttributeScope {
    SCOPE
  }

  @BeforeEach
  void beforeEach() {
    this.argumentDeserializer =
        Guice.createInjector(
                new GraphQlDeserializationRegistryModule(),
                new AbstractModule() {
                  @Override
                  protected void configure() {
                    Multibinder.newSetBinder(binder(), ArgumentDeserializationConfig.class)
                        .addBinding()
                        .to(FilterArgumentDeserializationConfig.class);
                    bind(Key.get(new TypeLiteral<Class<? extends AttributeScope>>() {}))
                        .toInstance(TestAttributeScope.class);
                  }
                })
            .getInstance(ArgumentDeserializer.class);
  }

  @Test
  void deserializesValueIfPresent() {
    Map<String, Object> argMap =
        Map.of(
            FilterArgument.ARGUMENT_NAME,
            List.of(
                Map.of(
                    FilterArgument.FILTER_ARGUMENT_KEY,
                    "fooKey",
                    FilterArgument.FILTER_ARGUMENT_OPERATOR,
                    FilterOperatorType.EQUALS,
                    FilterArgument.FILTER_ARGUMENT_TYPE,
                    FilterType.ATTRIBUTE,
                    FilterArgument.FILTER_ARGUMENT_VALUE,
                    "fooValue",
                    FilterArgument.FILTER_ARGUMENT_ID_TYPE,
                    TestAttributeScope.SCOPE)));

    List<FilterArgument> result =
        this.argumentDeserializer.deserializeObjectList(argMap, FilterArgument.class).orElseThrow();
    assertEquals(1, result.size());
    assertEquals("fooKey", result.get(0).key());
    assertEquals(FilterOperatorType.EQUALS, result.get(0).operator());
    assertEquals(FilterType.ATTRIBUTE, result.get(0).type());
    assertEquals("fooValue", result.get(0).value());
    assertEquals(TestAttributeScope.SCOPE, result.get(0).idScope());
  }
}
