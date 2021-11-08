package org.hypertrace.graphql.label.dao;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.graphql.label.schema.query.Label;
import org.hypertrace.graphql.label.schema.query.LabelResultSet;
import org.hypertrace.graphql.label.schema.shared.LabelData;
import org.hypertrace.label.config.service.v1.CreateLabelResponse;
import org.hypertrace.label.config.service.v1.GetLabelsResponse;
import org.hypertrace.label.config.service.v1.UpdateLabelResponse;

public class LabelResponseConverter {
  public Single<LabelResultSet> convert(List<Label> labels) {
    return Single.just(new ConvertedLabelResultSet(labels, labels.size(), labels.size()));
  }

  Single<LabelResultSet> convert(GetLabelsResponse response) {
    return convertToLabelList(response)
        .map(
            labelList ->
                new ConvertedLabelResultSet(labelList, labelList.size(), labelList.size()));
  }

  Single<List<Label>> convertToLabelList(GetLabelsResponse response) {
    return Single.just(
        response.getLabelsList().stream()
            .map(this::convertLabel)
            .collect(Collectors.toUnmodifiableList()));
  }

  Single<Label> convertLabel(CreateLabelResponse response) {
    return Single.just(convertLabel(response.getLabel()));
  }

  Single<Label> convertUpdateLabel(UpdateLabelResponse response) {
    return Single.just(convertLabel(response.getLabel()));
  }

  Label convertLabel(org.hypertrace.label.config.service.v1.Label label) {
    return new ConvertedLabel(
        label.getId(), convertLabelData(label.getData()), label.getCreatedByApplicationRuleId());
  }

  LabelData convertLabelData(org.hypertrace.label.config.service.v1.LabelData data) {
    return new ConvertedLabelData(data.getKey(), data.getColor(), data.getDescription());
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedLabelResultSet implements LabelResultSet {
    static final LabelResultSet EMPTY_LABEL_RESULT_SET =
        new ConvertedLabelResultSet(List.of(), 0, 0);
    List<Label> results;
    long count;
    long total;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedLabel implements Label {
    String id;
    LabelData data;
    String createdByRuleId;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedLabelData implements LabelData {
    String key;
    String color;
    String description;
  }
}
