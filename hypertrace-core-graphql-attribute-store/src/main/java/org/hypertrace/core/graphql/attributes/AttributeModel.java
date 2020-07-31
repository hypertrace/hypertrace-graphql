package org.hypertrace.core.graphql.attributes;

import java.util.List;

public interface AttributeModel {

  String id();

  AttributeModelScope scope();

  String key();

  String displayName();

  AttributeModelType type();

  String units();

  boolean requiresAggregation();

  List<AttributeModelMetricAggregationType> supportedMetricAggregationTypes();

  boolean groupable();
}
