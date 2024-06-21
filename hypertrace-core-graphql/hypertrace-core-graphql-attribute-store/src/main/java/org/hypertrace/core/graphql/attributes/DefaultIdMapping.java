package org.hypertrace.core.graphql.attributes;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
class DefaultIdMapping implements IdMapping {
  @Nonnull String containingScope;
  @Nonnull String idAttribute;
  @Nullable String foreignScope;

  @Override
  public String foreignScope() {
    return Optional.ofNullable(this.foreignScope).orElse(containingScope);
  }
}
