package org.hypertrace.core.graphql.common.schema;

import graphql.annotations.processor.typeFunctions.TypeFunction;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.schema.typefunctions.AttributeScopeDynamicEnum;
import org.hypertrace.core.graphql.common.schema.typefunctions.DateTimeScalar;
import org.hypertrace.core.graphql.common.schema.typefunctions.UnknownScalar;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

class CommonSchemaFragment implements GraphQlSchemaFragment {

  private final DateTimeScalar dateTimeScalar;
  private final UnknownScalar unknownScalar;
  private final AttributeScopeDynamicEnum attributeScopeDynamicEnum;

  @Inject
  CommonSchemaFragment(
      DateTimeScalar dateTimeScalar,
      UnknownScalar unknownScalar,
      AttributeScopeDynamicEnum attributeScopeDynamicEnum) {
    this.dateTimeScalar = dateTimeScalar;
    this.unknownScalar = unknownScalar;
    this.attributeScopeDynamicEnum = attributeScopeDynamicEnum;
  }

  @Override
  public String fragmentName() {
    return "Common Schema";
  }

  @Nonnull
  @Override
  public List<TypeFunction> typeFunctions() {
    return List.of(this.unknownScalar, this.dateTimeScalar, this.attributeScopeDynamicEnum);
  }
}
