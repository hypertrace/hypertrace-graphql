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

public class LabelResponseConverter {

  public LabelResultSet convert(List<String> labelIds, List<Label> labelList) {
    if (labelIds.isEmpty() || labelList.isEmpty()) {
      return DefaultLabelResultSet.EMPTY_LABEL_RESULT_SET;
    }
    List<Label> convertedLabels =
        labelList.stream()
            .filter(label -> labelIds.contains(label.id()))
            .collect(Collectors.toUnmodifiableList());
    return new DefaultLabelResultSet(
        convertedLabels, convertedLabels.size(), convertedLabels.size());
  }

  Single<LabelResultSet> convert(GetLabelsResponse response) {
    return convertToLabelList(response)
        .map(labelList -> new DefaultLabelResultSet(labelList, labelList.size(), labelList.size()));
  }

  Single<List<Label>> convertToLabelList(GetLabelsResponse response) {
    return Single.just(
        response.getLabelsList().stream()
            .map(label -> new DefaultLabel(label.getId(), label.getKey()))
            .collect(Collectors.toUnmodifiableList()));
  }

  Single<Label> convertLabel(CreateLabelResponse response) {
    return Single.just(new DefaultLabel(response.getLabel().getId(), response.getLabel().getKey()));
  }

  Single<Label> convertUpdateLabel(UpdateLabelResponse response) {
    return Single.just(new DefaultLabel(response.getLabel().getId(), response.getLabel().getKey()));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabelResultSet implements LabelResultSet {
    static final LabelResultSet EMPTY_LABEL_RESULT_SET = new DefaultLabelResultSet(List.of(), 0, 0);
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
