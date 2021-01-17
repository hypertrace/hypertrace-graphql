package org.hypertrace.graphql.spaces.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hypertrace.graphql.spaces.request.SpaceConfigRuleCreationRequest;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRuleAttributeValueRule;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRuleDefinition;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRuleType;
import org.hypertrace.spaces.config.service.v1.AttributeValueRuleData;
import org.hypertrace.spaces.config.service.v1.CreateRuleRequest;
import org.hypertrace.spaces.config.service.v1.GetRulesRequest;
import org.junit.jupiter.api.Test;

class SpaceConfigRulesRequestConverterTest {

  private final SpaceConfigRulesRequestConverter converter = new SpaceConfigRulesRequestConverter();

  @Test
  void convertsCreationRequest() {
    SpaceConfigRuleCreationRequest mockRequest = mock(SpaceConfigRuleCreationRequest.class);
    SpaceConfigRuleDefinition mockDefinition = mock(SpaceConfigRuleDefinition.class);
    SpaceConfigRuleAttributeValueRule mockAttributeValueRule =
        mock(SpaceConfigRuleAttributeValueRule.class);
    when(mockAttributeValueRule.attributeKey()).thenReturn("key");
    when(mockAttributeValueRule.attributeScope()).thenReturn("scope");

    when(mockDefinition.attributeValueRule()).thenReturn(mockAttributeValueRule);
    when(mockDefinition.type()).thenReturn(SpaceConfigRuleType.ATTRIBUTE_VALUE);
    when(mockRequest.ruleDefinition()).thenReturn(mockDefinition);

    CreateRuleRequest expectedRequest =
        CreateRuleRequest.newBuilder()
            .setAttributeValueRuleData(
                AttributeValueRuleData.newBuilder()
                    .setAttributeKey("key")
                    .setAttributeScope("scope")
                    .buildPartial())
            .build();

    assertEquals(expectedRequest, this.converter.convertCreationRequest(mockRequest));
  }

  @Test
  void convertsGetRequest() {
    GetRulesRequest expectedRequest = GetRulesRequest.newBuilder().build();

    assertEquals(expectedRequest, this.converter.convertGetRequest());
  }
}
