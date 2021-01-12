package org.hypertrace.core.graphql.common.schema;

import graphql.annotations.processor.typeFunctions.TypeFunction;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.hypertrace.core.graphql.common.schema.typefunctions.AttributeScopeDynamicEnum;
import org.hypertrace.core.graphql.common.schema.typefunctions.DateTimeScalar;
import org.hypertrace.core.graphql.common.schema.typefunctions.DurationScalar;
import org.hypertrace.core.graphql.common.schema.typefunctions.UnknownScalar;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;

class CommonSchemaFragment implements GraphQlSchemaFragment {

  private final DateTimeScalar dateTimeScalar;
  private final UnknownScalar unknownScalar;
  private final AttributeScopeDynamicEnum attributeScopeDynamicEnum;
  private final DurationScalar durationScalar;

  @Inject
  CommonSchemaFragment(
      DateTimeScalar dateTimeScalar,
      UnknownScalar unknownScalar,
      AttributeScopeDynamicEnum attributeScopeDynamicEnum,
      DurationScalar durationScalar) {
    this.dateTimeScalar = dateTimeScalar;
    this.unknownScalar = unknownScalar;
    this.attributeScopeDynamicEnum = attributeScopeDynamicEnum;
    this.durationScalar = durationScalar;
  }

  @Override
  public String fragmentName() {
    return "Common Schema";
  }

  @Nonnull
  @Override
  public List<TypeFunction> typeFunctions() {
    return List.of(this.unknownScalar, this.dateTimeScalar, this.attributeScopeDynamicEnum, this.durationScalar);
  }
}
