package org.hypertrace.graphql.label.dao;

import java.util.Optional;
import org.hypertrace.graphql.label.request.LabelCreateRequest;
import org.hypertrace.graphql.label.request.LabelUpdateRequest;
import org.hypertrace.label.config.service.v1.CreateLabelRequest;
import org.hypertrace.label.config.service.v1.LabelData;
import org.hypertrace.label.config.service.v1.UpdateLabelRequest;

public class LabelRequestConverter {
  CreateLabelRequest convertCreationRequest(LabelCreateRequest creationRequest) {
    LabelData.Builder dataBuilder = LabelData.newBuilder().setKey(creationRequest.label().key());
    Optional.ofNullable(creationRequest.label().color()).ifPresent(dataBuilder::setColor);
    Optional.ofNullable(creationRequest.label().description())
        .ifPresent(dataBuilder::setDescription);
    return CreateLabelRequest.newBuilder().setData(dataBuilder.build()).build();
  }

  UpdateLabelRequest convertUpdateRequest(LabelUpdateRequest updateRequest) {
    LabelData.Builder dataBuilder = LabelData.newBuilder().setKey(updateRequest.label().key());
    Optional.ofNullable(updateRequest.label().color()).ifPresent(dataBuilder::setColor);
    Optional.ofNullable(updateRequest.label().description()).ifPresent(dataBuilder::setDescription);
    return UpdateLabelRequest.newBuilder()
        .setId(updateRequest.label().id())
        .setData(dataBuilder.build())
        .build();
  }
}
