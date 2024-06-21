package org.hypertrace.core.graphql.common.utils.attributes;

/**
 * This serves as a temporary bridge to keep the external names of scopes unchanged until the
 * internal names can be changed to align.
 */
public interface AttributeScopeStringTranslator {

  String fromExternal(String external);

  String toExternal(String internal);
}
