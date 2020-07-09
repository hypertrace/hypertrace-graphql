package org.hypertrace.core.graphql.atttributes.scopes;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.utils.Converter;

class HypertraceCoreAttributeScopeConverter
    implements Converter<AttributeModelScope, AttributeScope> {

  @Override
  public Single<AttributeScope> convert(AttributeModelScope scope) {
    switch (scope) {
      case TRACE:
        return Single.just(HypertraceCoreAttributeScope.TRACE);
      case SPAN:
        return Single.just(HypertraceCoreAttributeScope.SPAN);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format("Unrecognized attribute scope %s", scope.name())));
    }
  }
}
