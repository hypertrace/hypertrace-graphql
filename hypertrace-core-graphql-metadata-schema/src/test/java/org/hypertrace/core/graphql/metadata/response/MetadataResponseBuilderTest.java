package org.hypertrace.core.graphql.metadata.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeModelMetricAggregationType;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeScopeStringTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetadataResponseBuilderTest {

  private MetadataResponseBuilder builder;
  private List<AttributeModel> models;
  @Mock private Converter<AttributeModelType, AttributeType> mockTypeConverter;

  @Mock
  private Converter<AttributeModelMetricAggregationType, MetricAggregationType>
      mockAggregationTypeConverter;

  @Mock private AttributeScopeStringTranslator mockScopeTranslator;

  @BeforeEach
  void beforeEach() {
    this.builder =
        new MetadataResponseBuilder(
            this.mockTypeConverter, this.mockAggregationTypeConverter, this.mockScopeTranslator);
    AttributeModel mockModel = mock(AttributeModel.class);
    when(mockModel.scope()).thenReturn("TRACE");
    when(mockModel.key()).thenReturn("key");
    when(mockModel.displayName()).thenReturn("display name");
    when(mockModel.type()).thenReturn(AttributeModelType.STRING);
    when(mockModel.units()).thenReturn("unit");
    when(mockModel.groupable()).thenReturn(true);
    when(mockModel.requiresAggregation()).thenReturn(true);
    when(mockModel.supportedMetricAggregationTypes())
        .thenReturn(
            List.of(
                AttributeModelMetricAggregationType.SUM, AttributeModelMetricAggregationType.AVG));
    when(this.mockTypeConverter.convert(eq(AttributeModelType.STRING)))
        .thenReturn(Single.just(AttributeType.STRING));
    when(this.mockAggregationTypeConverter.convert(eq(AttributeModelMetricAggregationType.SUM)))
        .thenReturn(Single.just(MetricAggregationType.SUM));
    when(this.mockAggregationTypeConverter.convert(eq(AttributeModelMetricAggregationType.AVG)))
        .thenReturn(Single.just(MetricAggregationType.AVG));
    when(this.mockScopeTranslator.toExternal("TRACE")).thenReturn("TRACE_EXTERNAL");
    this.models = List.of(mockModel);
  }

  @Test
  void canBuildResponse() {
    assertEquals(
        List.of(
            DefaultAttributeMetadata.builder()
                .scope("TRACE_EXTERNAL")
                .name("key")
                .displayName("display name")
                .type(AttributeType.STRING)
                .units("unit")
                .onlyAggregationsAllowed(true)
                .supportedAggregations(
                    List.of(MetricAggregationType.SUM, MetricAggregationType.AVG))
                .groupable(true)
                .build()),
        this.builder.build(this.models).blockingGet());
  }

  @Test
  @SuppressWarnings("unchecked")
  void filtersAnyAggregationConversionErrors() {
    reset(this.mockAggregationTypeConverter);
    when(this.mockAggregationTypeConverter.convert(eq(AttributeModelMetricAggregationType.AVG)))
        .thenReturn(Single.just(MetricAggregationType.AVG));

    when(this.mockAggregationTypeConverter.convert(eq(AttributeModelMetricAggregationType.SUM)))
        .thenReturn(Single.error(new RuntimeException()));

    assertEquals(
        List.of(
            DefaultAttributeMetadata.builder()
                .scope("TRACE_EXTERNAL")
                .name("key")
                .displayName("display name")
                .type(AttributeType.STRING)
                .units("unit")
                .onlyAggregationsAllowed(true)
                .supportedAggregations(List.of(MetricAggregationType.AVG))
                .groupable(true)
                .build()),
        this.builder.build(this.models).blockingGet());
  }
}
