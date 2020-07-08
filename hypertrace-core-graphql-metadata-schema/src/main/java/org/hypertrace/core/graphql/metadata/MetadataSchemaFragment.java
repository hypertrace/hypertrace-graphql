package org.hypertrace.core.graphql.metadata;

import graphql.annotations.processor.typeFunctions.TypeFunction;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.schema.typefunctions.AttributeScopeDynamicEnum;
import org.hypertrace.core.graphql.metadata.schema.MetadataSchema;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

public class MetadataSchemaFragment implements GraphQlSchemaFragment {

  private final TypeFunction attributeScopeDynamicEnum;

  @Inject
  MetadataSchemaFragment(AttributeScopeDynamicEnum attributeScopeDynamicEnum) {
    this.attributeScopeDynamicEnum = attributeScopeDynamicEnum;
  }

  @Override
  public String fragmentName() {
    return "Metadata schema";
  }

  @Override
  public Class<MetadataSchema> annotatedQueryClass() {
    return MetadataSchema.class;
  }

  @Nonnull
  @Override
  public List<TypeFunction> typeFunctions() {
    return List.of(this.attributeScopeDynamicEnum);
  }
}
