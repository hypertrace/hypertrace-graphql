package org.hypertrace.core.graphql.metadata.schema;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;

@GraphQLName(AttributeMetadata.TYPE_NAME)
public interface AttributeMetadata {
  String TYPE_NAME = "AttributeMetadata";
  String ATTRIBUTE_METADATA_SCOPE_NAME = "scope";
  String ATTRIBUTE_METADATA_NAME_NAME = "name";
  String ATTRIBUTE_METADATA_DISPLAY_NAME = "displayName";
  String ATTRIBUTE_METADATA_TYPE_NAME = "type";
  String ATTRIBUTE_METADATA_UNITS_NAME = "units";
  String ATTRIBUTE_METADATA_ONLY_SUPPORTS_AGGREGATION_NAME = "onlySupportsAggregation";
  String ATTRIBUTE_METADATA_ONLY_SUPPORTS_GROUPING_NAME = "onlySupportsGrouping";
  String ATTRIBUTE_METADATA_SUPPORTED_AGGREGATIONS_NAME = "supportedAggregations";
  String ATTRIBUTE_METADATA_GROUPABLE_NAME = "groupable";
  String ATTRIBUTE_METADATA_IS_CUSTOM = "isCustom";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_SCOPE_NAME)
  String scope();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_NAME_NAME)
  String name();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_DISPLAY_NAME)
  String displayName();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_TYPE_NAME)
  AttributeType type();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_UNITS_NAME)
  String units();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_ONLY_SUPPORTS_GROUPING_NAME)
  @GraphQLDescription("Signifies an attribute is only available in a grouped query")
  boolean onlySupportsGrouping();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_ONLY_SUPPORTS_AGGREGATION_NAME)
  @GraphQLDescription("Signifies an attribute is only available as an aggregation on all queries")
  boolean onlySupportsAggregation();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_SUPPORTED_AGGREGATIONS_NAME)
  List<MetricAggregationType> supportedAggregations();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_GROUPABLE_NAME)
  boolean groupable();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_IS_CUSTOM)
  boolean isCustom();
}
