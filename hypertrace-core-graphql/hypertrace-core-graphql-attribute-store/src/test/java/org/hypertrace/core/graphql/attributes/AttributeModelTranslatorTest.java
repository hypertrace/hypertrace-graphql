package org.hypertrace.core.graphql.attributes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import org.hypertrace.core.attribute.service.v1.AggregateFunction;
import org.hypertrace.core.attribute.service.v1.AttributeKind;
import org.hypertrace.core.attribute.service.v1.AttributeMetadata;
import org.hypertrace.core.attribute.service.v1.AttributeScope;
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
            .setScope(AttributeScope.TRACE)
            .setKey("key")
            .setDisplayName("display name")
            .setValueKind(AttributeKind.TYPE_STRING)
            .setUnit("unit")
            .setOnlyAggregationsAllowed(true)
            .addAllSupportedAggregations(List.of(AggregateFunction.SUM, AggregateFunction.AVG))
            .build();

    this.expectedModel =
        DefaultAttributeModel.builder()
            .id("id")
            .scope(AttributeModelScope.TRACE)
            .key("key")
            .displayName("display name")
            .type(AttributeModelType.STRING)
            .units("unit")
            .requiresAggregation(true)
            .supportedMetricAggregationTypes(
                List.of(
                    AttributeModelMetricAggregationType.SUM,
                    AttributeModelMetricAggregationType.AVG))
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
}
