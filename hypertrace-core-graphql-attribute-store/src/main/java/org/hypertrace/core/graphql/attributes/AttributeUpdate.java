package org.hypertrace.core.graphql.attributes;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.attribute.service.v1.Update;

@Value
@Builder
@Accessors(fluent = true)
public class AttributeUpdate {
  private static final List<UpdateMapper<?>> MAPPER_LIST =
      List.of(new UpdateMapper<>(AttributeUpdate::displayName, Update.Builder::setDisplayName));

  @Nullable String displayName;

  List<Update> buildUpdates() {
    return MAPPER_LIST.stream()
        .map(mapper -> mapper.apply(this))
        .flatMap(Optional::stream)
        .collect(toUnmodifiableList());
  }

  @Value
  private static class UpdateMapper<T> implements Function<AttributeUpdate, Optional<Update>> {
    Function<AttributeUpdate, T> valueAccessor;
    BiFunction<Update.Builder, T, Update.Builder> valueSetter;

    @Override
    public Optional<Update> apply(final AttributeUpdate attributeUpdate) {
      final T value = valueAccessor.apply(attributeUpdate);
      if (Objects.isNull(value)) {
        return Optional.empty();
      }

      final Update.Builder builder = Update.newBuilder();
      final Update update = valueSetter.apply(builder, value).build();
      return Optional.of(update);
    }
  }
}
