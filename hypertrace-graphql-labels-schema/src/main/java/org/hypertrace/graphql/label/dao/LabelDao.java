package org.hypertrace.graphql.label.dao;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.graphql.label.request.LabelCreateRequest;
import org.hypertrace.graphql.label.request.LabelUpdateRequest;
import org.hypertrace.graphql.label.schema.Label;
import org.hypertrace.graphql.label.schema.LabelResultSet;

public interface LabelDao {
  Single<LabelResultSet> getLabelResultSet(ContextualRequest request);

  Single<Label> createLabel(LabelCreateRequest request);

  Single<Label> updateLabel(LabelUpdateRequest request);

  Single<List<Label>> getLabels(ContextualRequest context);
}
