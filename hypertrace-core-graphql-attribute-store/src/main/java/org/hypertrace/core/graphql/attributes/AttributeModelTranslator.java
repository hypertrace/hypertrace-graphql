package org.hypertrace.core.graphql.attributes;

import java.util.List;
import java.util.Optional;
import java.util.UnknownFormatConversionException;
import java.util.stream.Collectors;
import org.hypertrace.core.attribute.service.v1.AggregateFunction;
import org.hypertrace.core.attribute.service.v1.AttributeKind;
import org.hypertrace.core.attribute.service.v1.AttributeMetadata;
import org.hypertrace.core.attribute.service.v1.AttributeScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AttributeModelTranslator {
  private static final Logger LOGGER = LoggerFactory.getLogger(AttributeModelTranslator.class);

  public Optional<AttributeModel> translate(AttributeMetadata attributeMetadata) {
    try {
      return Optional.of(
          new DefaultAttributeModel(
              attributeMetadata.getId(),
              this.convertScope(attributeMetadata.getScope()),
              attributeMetadata.getKey(),
              attributeMetadata.getDisplayName(),
              this.convertType(attributeMetadata.getValueKind()),
              attributeMetadata.getUnit(),
              attributeMetadata.getOnlyAggregationsAllowed(),
              this.convertMetricAggregationTypes(attributeMetadata.getSupportedAggregationsList()),
              attributeMetadata.getGroupable()));
    } catch (Exception e) {
      LOGGER.warn("Dropping attribute {} : {}", attributeMetadata.getId(), e.getMessage());
      return Optional.empty();
    }
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
      case AGG_UNDEFINED:
      case UNRECOGNIZED:
      default:
        throw new UnknownFormatConversionException(
            String.format("Unrecognized aggregate function %s", aggregateFunction.name()));
    }
  }

  private AttributeModelType convertType(AttributeKind kind) {
    switch (kind) {
      case TYPE_STRING:
        return AttributeModelType.STRING;
      case TYPE_BOOL:
        return AttributeModelType.BOOLEAN;
      case TYPE_INT64:
        return AttributeModelType.LONG;
      case TYPE_DOUBLE:
        return AttributeModelType.DOUBLE;
      case TYPE_TIMESTAMP:
        return AttributeModelType.TIMESTAMP;
      case TYPE_STRING_MAP:
        return AttributeModelType.STRING_MAP;
      case KIND_UNDEFINED:
      case UNRECOGNIZED:
      case TYPE_BYTES:
      case TYPE_STRING_ARRAY:
      case TYPE_INT64_ARRAY:
      case TYPE_DOUBLE_ARRAY:
      case TYPE_BOOL_ARRAY:
      default:
        throw new UnknownFormatConversionException(
            String.format("Unrecognized attribute kind %s", kind.name()));
    }
  }

  private AttributeModelScope convertScope(AttributeScope scope) {
    switch (scope) {
      case TRACE:
        return AttributeModelScope.TRACE;
      case EVENT:
        return AttributeModelScope.SPAN;
      case INTERACTION:
        return AttributeModelScope.INTERACTION;
      case DOMAIN_EVENT:
        return AttributeModelScope.DOMAIN_EVENT;
      case API:
        return AttributeModelScope.API;
      case SERVICE:
        return AttributeModelScope.SERVICE;
      case DOMAIN:
        return AttributeModelScope.DOMAIN;
      case TRANSACTION:
        return AttributeModelScope.TRANSACTION;
      case SESSION:
        return AttributeModelScope.SESSION;
      case API_TRACE:
        return AttributeModelScope.API_TRACE;
      case BACKEND:
        return AttributeModelScope.BACKEND;
      case BACKEND_TRACE:
        return AttributeModelScope.BACKEND_TRACE;
      case ACTOR:
        return AttributeModelScope.ACTOR;
      case SCOPE_UNDEFINED:
      case ENTITY:
      case EVENT_EVENT_EDGE:
      case ENTITY_EVENT_EDGE:
      case ENTITY_ENTITY_EDGE:
      case THREAT_ACTOR:
      case CLUSTERS_SNAPSHOT:
      case UNRECOGNIZED:
      default:
        throw new UnknownFormatConversionException(
            String.format("Unrecognized attribute scope %s", scope.name()));
    }
  }
}
