package org.hypertrace.core.graphql.atttributes.scopes;

import io.reactivex.rxjava3.core.Single;
import java.util.UnknownFormatConversionException;
import org.hypertrace.core.graphql.attributes.AttributeModelScope;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.utils.Converter;

class HypertraceCoreAttributeModelScopeConverter
    implements Converter<AttributeScope, AttributeModelScope> {

  @Override
  public Single<AttributeModelScope> convert(AttributeScope scope) {
    switch ((HypertraceCoreAttributeScope) scope) {
      case TRACE:
        return Single.just(AttributeModelScope.TRACE);
      case SPAN:
        return Single.just(AttributeModelScope.SPAN);
      default:
        return Single.error(
            new UnknownFormatConversionException(
                String.format("Unrecognized attribute scope %s", scope)));
    }
  }
}
