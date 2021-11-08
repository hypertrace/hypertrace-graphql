package org.hypertrace.graphql.label.dao;

import java.util.Objects;
import org.hypertrace.graphql.label.request.LabelCreateRequest;
import org.hypertrace.graphql.label.request.LabelUpdateRequest;
import org.hypertrace.label.config.service.v1.CreateLabelRequest;
import org.hypertrace.label.config.service.v1.LabelData;
import org.hypertrace.label.config.service.v1.UpdateLabelRequest;

public class LabelRequestConverter {
  CreateLabelRequest convertCreationRequest(LabelCreateRequest creationRequest) {
    CreateLabelRequest convertedRequest =
        CreateLabelRequest.newBuilder()
            .setData(convertLabelData(creationRequest.label().data()))
            .build();
    if (Objects.nonNull(creationRequest.label().createdByRuleId())) {
      convertedRequest =
          convertedRequest.toBuilder()
              .setCreatedByApplicationRuleId(
                  Objects.requireNonNull(creationRequest.label().createdByRuleId()))
              .build();
    }
    return convertedRequest;
  }

  UpdateLabelRequest convertUpdateRequest(LabelUpdateRequest updateRequest) {
    return UpdateLabelRequest.newBuilder()
        .setId(updateRequest.label().id())
        .setData(convertLabelData(updateRequest.label().data()))
        .build();
  }

  LabelData convertLabelData(org.hypertrace.graphql.label.schema.shared.LabelData data) {
    LabelData convertedData = LabelData.newBuilder().setKey(data.key()).build();
    if (Objects.nonNull(data.color())) {
      convertedData =
          convertedData.toBuilder().setColor(Objects.requireNonNull(data.color())).build();
    }
    if (Objects.nonNull(data.description())) {
      convertedData =
          convertedData.toBuilder()
              .setDescription(Objects.requireNonNull(data.description()))
              .build();
    }
    return convertedData;
  }
}
