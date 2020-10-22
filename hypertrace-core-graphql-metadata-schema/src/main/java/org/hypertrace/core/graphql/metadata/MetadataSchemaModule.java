package org.hypertrace.core.graphql.metadata;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import java.util.Optional;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

public class MetadataSchemaModule extends AbstractModule {

  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), GraphQlSchemaFragment.class)
        .addBinding()
        .to(MetadataSchemaFragment.class);

    requireBinding(AttributeStore.class);

    requireBinding(Key.get(new TypeLiteral<Converter<String, Optional<AttributeScope>>>() {}));
    requireBinding(Key.get(new TypeLiteral<Converter<AttributeModelType, AttributeType>>() {}));
    requireBinding(
        Key.get(
            new TypeLiteral<
                Converter<AttributeModelMetricAggregationType, MetricAggregationType>>() {}));
  }
}
