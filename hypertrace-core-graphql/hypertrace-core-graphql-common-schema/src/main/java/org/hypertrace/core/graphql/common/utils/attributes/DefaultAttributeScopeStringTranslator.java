package org.hypertrace.core.graphql.common.utils.attributes;

import org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString;

class DefaultAttributeScopeStringTranslator implements AttributeScopeStringTranslator {
  private static final String SPAN_SCOPE_EXTERNAL_NAME = "SPAN";

  @Override
  public String fromExternal(String external) {
    if (SPAN_SCOPE_EXTERNAL_NAME.equals(external)) {
      return HypertraceCoreAttributeScopeString.SPAN;
    }
    return external;
  }

  @Override
  public String toExternal(String internal) {
    if (HypertraceCoreAttributeScopeString.SPAN.equals(internal)) {
      return SPAN_SCOPE_EXTERNAL_NAME;
    }
    return internal;
  }
}
