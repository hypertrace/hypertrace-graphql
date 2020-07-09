package org.hypertrace.graphql.entity.type;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.graphql.entity.schema.EntityType;

public class HypertraceEntityTypeModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Key.get(new TypeLiteral<Class<? extends EntityType>>() {}))
        .toInstance(HypertraceEntityType.class);

    bind(Key.get(new TypeLiteral<Converter<EntityType, AttributeModelScope>>() {}))
        .to(HypertraceEntityTypeScopeConverter.class);
    bind(Key.get(new TypeLiteral<Converter<EntityType, String>>() {}))
        .to(HypertraceEntityTypeStringConverter.class);
    bind(Key.get(new TypeLiteral<Converter<String, EntityType>>() {}))
        .to(HypertraceStringEntityTypeConverter.class);
  }
}
