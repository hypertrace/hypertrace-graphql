package org.hypertrace.core.graphql.attributes;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Builder(toBuilder = true)
@Accessors(fluent = true)
class DefaultAttributeModel implements AttributeModel {
  String id;
  String scope;
  String key;
  String displayName;
  AttributeModelType type;
  String units;
  boolean onlySupportsGrouping;
  boolean onlySupportsAggregation;
  List<AttributeModelMetricAggregationType> supportedMetricAggregationTypes;
  boolean groupable;
}
