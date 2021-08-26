package org.hypertrace.graphql.label.dao;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.label.schema.LabelResultSet;

public interface LabelDao {

  Single<LabelResultSet> getLabels(ContextualRequest request);
}
