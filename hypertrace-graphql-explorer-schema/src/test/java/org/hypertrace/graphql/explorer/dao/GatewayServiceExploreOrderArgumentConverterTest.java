package org.hypertrace.graphql.explorer.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Optional;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderDirection;
import org.hypertrace.core.graphql.common.utils.Converter;
import org.hypertrace.gateway.service.v1.common.ColumnIdentifier;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.OrderByExpression;
import org.hypertrace.gateway.service.v1.common.SortOrder;
import org.hypertrace.gateway.service.v1.explore.ColumnName;
import org.hypertrace.graphql.explorer.request.ExploreOrderArgument;
import org.hypertrace.graphql.explorer.request.ExploreOrderArgument.ExploreOrderArgumentType;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GatewayServiceExploreOrderArgumentConverterTest {

  GatewayServiceExploreOrderArgumentConverter converter;

  @Mock
  Converter<List<AttributeAssociation<AggregatableOrderArgument>>, List<OrderByExpression>>
      mockAttributeOrderConverter;

  @Mock Converter<OrderDirection, SortOrder> mockSortOrderConverter;

  @BeforeEach
  void beforeEach() {
    this.converter =
        new GatewayServiceExploreOrderArgumentConverter(
            mockAttributeOrderConverter, mockSortOrderConverter);
  }

  @Test
  void testConvertArgumentList() {
    OrderByExpression expectedFirstOrder =
        OrderByExpression.newBuilder()
            .setOrder(SortOrder.DESC)
            .setExpression(
                Expression.newBuilder()
                    .setColumnIdentifier(
                        ColumnIdentifier.newBuilder()
                            .setColumnName(ColumnName.INTERVAL_START_TIME.name())))
            .build();
    OrderByExpression expectedSecondOrder =
        OrderByExpression.newBuilder()
            .setOrder(SortOrder.ASC)
            .setExpression(
                Expression.newBuilder()
                    .setColumnIdentifier(ColumnIdentifier.newBuilder().setColumnName("other")))
            .build();
    AggregatableOrderArgument firstArg = mock(AggregatableOrderArgument.class);
    when(firstArg.direction()).thenReturn(OrderDirection.DESC);
    when(mockSortOrderConverter.convert(OrderDirection.DESC))
        .thenReturn(Single.just(SortOrder.DESC));
    ExploreOrderArgument firstExplorerArg = mock(ExploreOrderArgument.class);
    when(firstExplorerArg.type()).thenReturn(ExploreOrderArgumentType.INTERVAL_START);
    when(firstExplorerArg.argument()).thenReturn(firstArg);

    AggregatableOrderArgument secondArg = mock(AggregatableOrderArgument.class);
    AttributeModel secondArgAttribute = mock(AttributeModel.class);
    ExploreOrderArgument secondExplorerArg = mock(ExploreOrderArgument.class);
    when(secondExplorerArg.type()).thenReturn(ExploreOrderArgumentType.ATTRIBUTE);
    when(secondExplorerArg.argument()).thenReturn(secondArg);
    when(secondExplorerArg.attribute()).thenReturn(Optional.of(secondArgAttribute));

    when(this.mockAttributeOrderConverter.convert(
            List.of(AttributeAssociation.of(secondArgAttribute, secondArg))))
        .thenReturn(Single.just(List.of(expectedSecondOrder)));

    assertEquals(
        List.of(expectedFirstOrder, expectedSecondOrder),
        this.converter.convert(List.of(firstExplorerArg, secondExplorerArg)).blockingGet());
  }
}
