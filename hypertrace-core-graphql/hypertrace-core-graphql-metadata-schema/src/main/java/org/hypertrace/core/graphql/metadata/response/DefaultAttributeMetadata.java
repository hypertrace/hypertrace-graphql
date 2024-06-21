package org.hypertrace.core.graphql.metadata.response;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;
import org.hypertrace.core.graphql.metadata.schema.AttributeMetadata;

@Value
@Builder
@Accessors(fluent = true)
@AllArgsConstructor(access = PRIVATE)
class DefaultAttributeMetadata implements AttributeMetadata {
  String scope;
  String name;
  String displayName;
  AttributeType type;
  String units;
  boolean onlySupportsGrouping;
  boolean onlySupportsAggregation;
  List<MetricAggregationType> supportedAggregations;
  boolean groupable;
  boolean isCustom;
}
