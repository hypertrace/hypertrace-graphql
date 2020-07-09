package org.hypertrace.core.graphql.common.utils.attributes;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.utils.Converter;

public class AttributeUtilsModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Key.get(new TypeLiteral<Converter<AttributeModelType, AttributeType>>() {}))
        .to(AttributeTypeConverter.class);
    bind(Key.get(
            new TypeLiteral<
                Converter<AttributeModelMetricAggregationType, MetricAggregationType>>() {}))
        .to(MetricAggregationTypeConverter.class);
    bind(Key.get(
            new TypeLiteral<
                Converter<MetricAggregationType, AttributeModelMetricAggregationType>>() {}))
        .to(AttributeModelMetricAggregationTypeConverter.class);

    bind(AttributeAssociator.class).to(DefaultAttributeAssociator.class);

    requireBinding(AttributeStore.class);
  }
}
