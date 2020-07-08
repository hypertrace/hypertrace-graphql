package org.hypertrace.core.graphql.common.request;

import org.hypertrace.core.graphql.attributes.AttributeModel;

public interface AttributeRequest {

  AttributeModel attribute();

  String alias();
}
