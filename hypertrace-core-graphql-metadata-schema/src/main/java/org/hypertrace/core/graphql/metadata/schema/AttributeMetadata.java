package org.hypertrace.core.graphql.metadata.schema;

import graphql.annotations.annotationTypes.GraphQLDeprecate;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeType;
import org.hypertrace.core.graphql.common.schema.attributes.MetricAggregationType;

@GraphQLName(AttributeMetadata.TYPE_NAME)
public interface AttributeMetadata {
  String TYPE_NAME = "AttributeMetadata";
  String ATTRIBUTE_METADATA_SCOPE_NAME = "scope";
  String ATTRIBUTE_METADATA_SCOPE_STRING_NAME = "scopeString";
  String ATTRIBUTE_METADATA_NAME_NAME = "name";
  String ATTRIBUTE_METADATA_DISPLAY_NAME = "displayName";
  String ATTRIBUTE_METADATA_TYPE_NAME = "type";
  String ATTRIBUTE_METADATA_UNITS_NAME = "units";
  String ATTRIBUTE_METADATA_ONLY_AGGREGATIONS_ALLOWED_NAME = "onlyAggregationsAllowed";
  String ATTRIBUTE_METADATA_SUPPORTED_AGGREGATIONS_NAME = "supportedAggregations";
  String ATTRIBUTE_METADATA_GROUPABLE_NAME = "groupable";

  @GraphQLField
  @GraphQLName(ATTRIBUTE_METADATA_SCOPE_NAME)
  @GraphQLDeprecate
  @Deprecated
  AttributeScope scope();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_SCOPE_STRING_NAME)
  String scopeString();

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
  @GraphQLName(ATTRIBUTE_METADATA_ONLY_AGGREGATIONS_ALLOWED_NAME)
  boolean onlyAggregationsAllowed();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_SUPPORTED_AGGREGATIONS_NAME)
  List<MetricAggregationType> supportedAggregations();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_METADATA_GROUPABLE_NAME)
  boolean groupable();
}
