package org.hypertrace.core.graphql.attributes;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Builder
@Accessors(fluent = true)
class DefaultAttributeModel implements AttributeModel {
  String id;
  AttributeModelScope scope;
  String key;
  String displayName;
  AttributeModelType type;
  String units;
  boolean requiresAggregation;
  List<AttributeModelMetricAggregationType> supportedMetricAggregationTypes;
  boolean groupable;
}
