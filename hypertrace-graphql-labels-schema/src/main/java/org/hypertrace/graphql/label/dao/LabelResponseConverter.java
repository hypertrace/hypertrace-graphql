package org.hypertrace.graphql.label.dao;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.graphql.label.schema.Label;
import org.hypertrace.graphql.label.schema.LabelResultSet;
import org.hypertrace.label.config.service.v1.CreateLabelResponse;
import org.hypertrace.label.config.service.v1.GetLabelsResponse;
import org.hypertrace.label.config.service.v1.UpdateLabelResponse;

class LabelResponseConverter {

  Single<LabelResultSet> convert(GetLabelsResponse response) {
    List<Label> labelList =
        response.getLabelsList().stream()
            .map(label -> new DefaultLabel(label.getId(), label.getKey()))
            .collect(Collectors.toUnmodifiableList());
    return Single.just(new DefaultLabelResultSet(labelList, labelList.size(), labelList.size()));
  }

  Single<Label> convertLabel(CreateLabelResponse response) {
    return Single.just(new DefaultLabel(response.getLabel().getId(), response.getLabel().getKey()));
  }

  Single<Boolean> buildDeleteResponse() {
    return Single.just(true);
  }

  Single<Label> convertUpdateLabel(UpdateLabelResponse response) {
    return Single.just(new DefaultLabel(response.getLabel().getId(), response.getLabel().getKey()));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabelResultSet implements LabelResultSet {
    List<Label> results;
    long count;
    long total;
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabel implements Label {
    String id;
    String key;
  }
}
