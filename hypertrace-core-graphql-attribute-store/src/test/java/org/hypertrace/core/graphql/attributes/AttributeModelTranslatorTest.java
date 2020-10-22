package org.hypertrace.core.graphql.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import org.hypertrace.core.attribute.service.v1.AggregateFunction;
import org.hypertrace.core.attribute.service.v1.AttributeKind;
import org.hypertrace.core.attribute.service.v1.AttributeMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttributeModelTranslatorTest {

  private AttributeModelTranslator translator;
  private AttributeMetadata metadata;
  private AttributeModel expectedModel;

  @BeforeEach
  void beforeEach() {
    this.translator = new AttributeModelTranslator();
    this.metadata =
        AttributeMetadata.newBuilder()
            .setId("id")
            .setScopeString("TRACE")
            .setKey("key")
            .setDisplayName("display name")
            .setValueKind(AttributeKind.TYPE_STRING)
            .setUnit("unit")
            .setOnlyAggregationsAllowed(true)
            .addAllSupportedAggregations(List.of(AggregateFunction.SUM, AggregateFunction.AVG))
            .setGroupable(true)
            .build();

    this.expectedModel =
        DefaultAttributeModel.builder()
            .id("id")
            .scope("TRACE")
            .key("key")
            .displayName("display name")
            .type(AttributeModelType.STRING)
            .units("unit")
            .requiresAggregation(true)
            .supportedMetricAggregationTypes(
                List.of(
                    AttributeModelMetricAggregationType.SUM,
                    AttributeModelMetricAggregationType.AVG))
            .groupable(true)
            .build();
  }

  @Test
  void canTranslateAttributeModel() {
    assertEquals(Optional.of(this.expectedModel), this.translator.translate(this.metadata));
  }

  @Test
  void returnsEmptyIfUnsupportedTranslation() {
    AttributeMetadata unsupportedMetadata =
        AttributeMetadata.newBuilder(this.metadata)
            .addSupportedAggregations(AggregateFunction.AGG_UNDEFINED)
            .build();
    assertEquals(Optional.empty(), this.translator.translate(unsupportedMetadata));
  }

  @Test
  void testStringArrayAttributeKindTranslation() {
    this.translator = new AttributeModelTranslator();
    this.metadata =
        AttributeMetadata.newBuilder()
            .setId("id")
            .setScopeString("TRACE")
            .setKey("key")
            .setDisplayName("display name")
            .setValueKind(AttributeKind.TYPE_STRING_ARRAY)
            .setUnit("unit")
            .setOnlyAggregationsAllowed(false)
            .addAllSupportedAggregations(
                List.of(AggregateFunction.DISTINCT_COUNT, AggregateFunction.AVGRATE))
            .setGroupable(false)
            .build();

    this.expectedModel =
        DefaultAttributeModel.builder()
            .id("id")
            .scope("TRACE")
            .key("key")
            .displayName("display name")
            .type(AttributeModelType.STRING_ARRAY)
            .units("unit")
            .requiresAggregation(false)
            .supportedMetricAggregationTypes(
                List.of(
                    AttributeModelMetricAggregationType.DISTINCT_COUNT,
                    AttributeModelMetricAggregationType.AVGRATE))
            .groupable(false)
            .build();

    assertEquals(Optional.of(this.expectedModel), this.translator.translate(this.metadata));
  }
}
