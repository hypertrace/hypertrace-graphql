package org.hypertrace.graphql.label.dao;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.label.request.LabelCreateRequest;
import org.hypertrace.graphql.label.request.LabelUpdateRequest;
import org.hypertrace.graphql.label.schema.query.Label;
import org.hypertrace.graphql.label.schema.query.LabelResultSet;

public interface LabelDao {
  Single<LabelResultSet> getLabels(ContextualRequest request);

  Single<Label> createLabel(LabelCreateRequest request);

  Single<Label> updateLabel(LabelUpdateRequest request);
}
