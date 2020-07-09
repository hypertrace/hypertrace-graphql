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
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.attributes.AttributeModelType;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetadataResponseBuilderTest {

  private MetadataResponseBuilder builder;
  private List<AttributeModel> models;
  @Mock private Converter<AttributeModelScope, AttributeScope> mockScopeConverter;
  @Mock private Converter<AttributeModelType, AttributeType> mockTypeConverter;
  @Mock private AttributeScope mockScope;

  @Mock
  private Converter<AttributeModelMetricAggregationType, MetricAggregationType>
      mockAggregationTypeConverter;

  @BeforeEach
  void beforeEach() {
    this.builder =
        new MetadataResponseBuilder(
            this.mockScopeConverter, this.mockTypeConverter, this.mockAggregationTypeConverter);
    AttributeModel mockModel = mock(AttributeModel.class);
    when(mockModel.scope()).thenReturn(AttributeModelScope.TRACE);
    when(mockModel.key()).thenReturn("key");
    when(mockModel.displayName()).thenReturn("display name");
    when(mockModel.type()).thenReturn(AttributeModelType.STRING);
    when(mockModel.units()).thenReturn("unit");
    when(mockModel.requiresAggregation()).thenReturn(true);
    when(mockModel.supportedMetricAggregationTypes())
        .thenReturn(
            List.of(
                AttributeModelMetricAggregationType.SUM, AttributeModelMetricAggregationType.AVG));
    when(this.mockScopeConverter.convert(eq(AttributeModelScope.TRACE)))
        .thenReturn(Single.just(this.mockScope));

    when(this.mockTypeConverter.convert(eq(AttributeModelType.STRING)))
        .thenReturn(Single.just(AttributeType.STRING));
    when(this.mockAggregationTypeConverter.convert(eq(AttributeModelMetricAggregationType.SUM)))
        .thenReturn(Single.just(MetricAggregationType.SUM));
    when(this.mockAggregationTypeConverter.convert(eq(AttributeModelMetricAggregationType.AVG)))
        .thenReturn(Single.just(MetricAggregationType.AVG));
    this.models = List.of(mockModel);
  }

  @Test
  void canBuildResponse() {
    assertEquals(
        List.of(
            DefaultAttributeMetadata.builder()
                .scope(this.mockScope)
                .name("key")
                .displayName("display name")
                .type(AttributeType.STRING)
                .units("unit")
                .onlyAggregationsAllowed(true)
                .supportedAggregations(
                    List.of(MetricAggregationType.SUM, MetricAggregationType.AVG))
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
                .scope(this.mockScope)
                .name("key")
                .displayName("display name")
                .type(AttributeType.STRING)
                .units("unit")
                .onlyAggregationsAllowed(true)
                .supportedAggregations(List.of(MetricAggregationType.AVG))
                .build()),
        this.builder.build(this.models).blockingGet());
  }
}
