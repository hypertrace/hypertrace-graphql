package org.hypertrace.core.graphql.metadata.schema;

import graphql.annotations.annotationTypes.GraphQLDataFetcher;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.List;
import org.hypertrace.core.graphql.metadata.fetcher.MetadataFetcher;

public interface MetadataSchema {
  String METADATA_SCHEMA_METADATA_NAME = "metadata";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(METADATA_SCHEMA_METADATA_NAME)
  @GraphQLDataFetcher(MetadataFetcher.class)
  List<AttributeMetadata> metadata();
}
