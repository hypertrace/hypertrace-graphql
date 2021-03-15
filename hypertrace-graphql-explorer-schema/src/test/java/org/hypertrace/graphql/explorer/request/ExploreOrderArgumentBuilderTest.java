package org.hypertrace.graphql.explorer.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Optional;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.explorer.request.ExploreOrderArgument.ExploreOrderArgumentType;
import org.hypertrace.graphql.explorer.schema.ExploreResult;
import org.hypertrace.graphql.metric.schema.argument.AggregatableOrderArgument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExploreOrderArgumentBuilderTest {

  private static final String MOCK_CONTEXT = "mock_context";

  @Mock AttributeStore mockAttributeStore;
  @Mock GraphQlRequestContext mockRequestContext;

  private ExploreOrderArgumentBuilder exploreOrderArgumentBuilder;

  @BeforeEach
  void beforeEach() {
    this.exploreOrderArgumentBuilder = new ExploreOrderArgumentBuilder(this.mockAttributeStore);
  }

  @Test
  void convertsList() {
    AggregatableOrderArgument firstArg = mock(AggregatableOrderArgument.class);
    when(firstArg.key()).thenReturn(ExploreResult.EXPLORE_RESULT_INTERVAL_START_KEY);
    AggregatableOrderArgument secondArg = mock(AggregatableOrderArgument.class);
    when(secondArg.key()).thenReturn("second");
    AttributeModel mockAttributeModel = mock(AttributeModel.class);
    when(mockAttributeStore.get(mockRequestContext, MOCK_CONTEXT, "second"))
        .thenReturn(Single.just(mockAttributeModel));

    List<ExploreOrderArgument> results =
        this.exploreOrderArgumentBuilder
            .buildList(this.mockRequestContext, MOCK_CONTEXT, List.of(firstArg, secondArg))
            .blockingGet();

    assertEquals(2, results.size());
    assertEquals(ExploreOrderArgumentType.INTERVAL_START, results.get(0).type());
    assertEquals(Optional.empty(), results.get(0).attribute());
    assertEquals(firstArg, results.get(0).argument());
    assertEquals(ExploreOrderArgumentType.ATTRIBUTE, results.get(1).type());
    assertEquals(Optional.of(mockAttributeModel), results.get(1).attribute());
    assertEquals(secondArg, results.get(1).argument());
  }
}
