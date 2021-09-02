package org.hypertrace.graphql.label.dao;

import org.hypertrace.graphql.label.request.LabelCreateRequest;
import org.hypertrace.graphql.label.request.LabelUpdateRequest;
import org.hypertrace.label.config.service.v1.CreateLabel;
import org.hypertrace.label.config.service.v1.CreateLabelRequest;
import org.hypertrace.label.config.service.v1.Label;
import org.hypertrace.label.config.service.v1.UpdateLabelRequest;

public class LabelRequestConverter {
  CreateLabelRequest convertCreationRequest(LabelCreateRequest creationRequest) {
    return CreateLabelRequest.newBuilder()
        .setLabel(CreateLabel.newBuilder().setKey(creationRequest.label().key()).build())
        .build();
  }

  UpdateLabelRequest convertUpdateRequest(LabelUpdateRequest updateRequest) {
    return UpdateLabelRequest.newBuilder()
        .setLabel(
            Label.newBuilder()
                .setKey(updateRequest.label().key())
                .setId(updateRequest.label().id())
                .build())
        .build();
  }
}
