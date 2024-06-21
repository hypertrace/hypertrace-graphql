package org.hypertrace.core.graphql.attributes;

import static org.hypertrace.core.attribute.service.v1.AttributeKind.TYPE_BOOL;
import static org.hypertrace.core.attribute.service.v1.AttributeKind.TYPE_BOOL_ARRAY;
import static org.hypertrace.core.attribute.service.v1.AttributeKind.TYPE_DOUBLE;
import static org.hypertrace.core.attribute.service.v1.AttributeKind.TYPE_DOUBLE_ARRAY;
import static org.hypertrace.core.attribute.service.v1.AttributeKind.TYPE_INT64;
import static org.hypertrace.core.attribute.service.v1.AttributeKind.TYPE_INT64_ARRAY;
import static org.hypertrace.core.attribute.service.v1.AttributeKind.TYPE_STRING;
import static org.hypertrace.core.attribute.service.v1.AttributeKind.TYPE_STRING_ARRAY;
import static org.hypertrace.core.attribute.service.v1.AttributeKind.TYPE_STRING_MAP;
import static org.hypertrace.core.attribute.service.v1.AttributeKind.TYPE_TIMESTAMP;

import com.google.common.collect.ImmutableBiMap;
import java.util.List;
import java.util.Optional;
import java.util.UnknownFormatConversionException;
import java.util.stream.Collectors;
import org.hypertrace.core.attribute.service.v1.AggregateFunction;
import org.hypertrace.core.attribute.service.v1.AttributeKind;
import org.hypertrace.core.attribute.service.v1.AttributeMetadata;
import org.hypertrace.core.attribute.service.v1.AttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeModelTranslator {
  private static final Logger LOGGER = LoggerFactory.getLogger(AttributeModelTranslator.class);
  private static final ImmutableBiMap<AttributeKind, AttributeModelType> TYPE_MAPPING =
      ImmutableBiMap.<AttributeKind, AttributeModelType>builder()
          .put(TYPE_STRING, AttributeModelType.STRING)
          .put(TYPE_BOOL, AttributeModelType.BOOLEAN)
          .put(TYPE_INT64, AttributeModelType.LONG)
          .put(TYPE_DOUBLE, AttributeModelType.DOUBLE)
          .put(TYPE_TIMESTAMP, AttributeModelType.TIMESTAMP)
          .put(TYPE_STRING_MAP, AttributeModelType.STRING_MAP)
          .put(TYPE_STRING_ARRAY, AttributeModelType.STRING_ARRAY)
          .put(TYPE_DOUBLE_ARRAY, AttributeModelType.DOUBLE_ARRAY)
          .put(TYPE_BOOL_ARRAY, AttributeModelType.BOOLEAN_ARRAY)
          .put(TYPE_INT64_ARRAY, AttributeModelType.LONG_ARRAY)
          .build();

  public Optional<AttributeModel> translate(AttributeMetadata attributeMetadata) {
    try {
      return Optional.of(
          DefaultAttributeModel.builder()
              .id(attributeMetadata.getId())
              .scope(attributeMetadata.getScopeString())
              .key(attributeMetadata.getKey())
              .displayName(attributeMetadata.getDisplayName())
              .type(this.convertType(attributeMetadata.getValueKind()))
              .units(attributeMetadata.getUnit())
              .onlySupportsGrouping(attributeMetadata.getOnlyAggregationsAllowed())
              .onlySupportsAggregation(attributeMetadata.getType().equals(AttributeType.METRIC))
              .supportedMetricAggregationTypes(
                  this.convertMetricAggregationTypes(
                      attributeMetadata.getSupportedAggregationsList()))
              .groupable(attributeMetadata.getGroupable())
              .isCustom(attributeMetadata.getCustom())
              .build());
    } catch (Exception e) {
      LOGGER.warn("Dropping attribute {} : {}", attributeMetadata.getId(), e.getMessage());
      return Optional.empty();
    }
  }

  @SuppressWarnings("unused")
  public AttributeKind convertType(AttributeModelType type) {
    return Optional.ofNullable(TYPE_MAPPING.inverse().get(type))
        .orElseThrow(
            () ->
                new UnknownFormatConversionException(
                    String.format("Unrecognized attribute type %s", type.name())));
  }

  private List<AttributeModelMetricAggregationType> convertMetricAggregationTypes(
      List<AggregateFunction> aggregationTypes) {
    return aggregationTypes.stream()
        .map(this::convertMetricAggregationType)
        .collect(Collectors.toUnmodifiableList());
  }

  private AttributeModelMetricAggregationType convertMetricAggregationType(
      AggregateFunction aggregateFunction) {
    switch (aggregateFunction) {
      case COUNT:
        return AttributeModelMetricAggregationType.COUNT;
      case AVG:
        return AttributeModelMetricAggregationType.AVG;
      case SUM:
        return AttributeModelMetricAggregationType.SUM;
      case MIN:
        return AttributeModelMetricAggregationType.MIN;
      case MAX:
        return AttributeModelMetricAggregationType.MAX;
      case AVGRATE:
        return AttributeModelMetricAggregationType.AVGRATE;
      case PERCENTILE:
        return AttributeModelMetricAggregationType.PERCENTILE;
      case DISTINCT_COUNT:
        return AttributeModelMetricAggregationType.DISTINCT_COUNT;
      case DISTINCT_ARRAY:
        return AttributeModelMetricAggregationType.DISTINCT_ARRAY;
      case AGG_UNDEFINED:
      case UNRECOGNIZED:
      default:
        throw new UnknownFormatConversionException(
            String.format("Unrecognized aggregate function %s", aggregateFunction.name()));
    }
  }

  private AttributeModelType convertType(AttributeKind kind) {
    return Optional.ofNullable(TYPE_MAPPING.get(kind))
        .orElseThrow(
            () ->
                new UnknownFormatConversionException(
                    String.format("Unrecognized attribute kind %s", kind.name())));
  }
}
