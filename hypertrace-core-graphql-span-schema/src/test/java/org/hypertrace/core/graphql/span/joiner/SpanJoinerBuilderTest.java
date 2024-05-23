package org.hypertrace.core.graphql.span.joiner;

import static java.util.Collections.emptyList;
import static java.util.Map.entry;
import static org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString.SPAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeAssociation;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.request.ResultSetRequest;
import org.hypertrace.core.graphql.common.request.ResultSetRequestBuilder;
import org.hypertrace.core.graphql.common.schema.arguments.TimeRangeArgument;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.order.OrderArgument;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.log.event.schema.LogEventResultSet;
import org.hypertrace.core.graphql.span.dao.SpanDao;
import org.hypertrace.core.graphql.span.joiner.SpanJoiner.SpanIdGetter;
import org.hypertrace.core.graphql.span.request.SpanRequest;
import org.hypertrace.core.graphql.span.schema.Span;
import org.hypertrace.core.graphql.span.schema.SpanResultSet;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SpanJoinerBuilderTest {

  private static final String FIRST_SPAN_ID = "spanId1";
  private static final String SECOND_SPAN_ID = "spanId2";

  @Mock private SpanDao mockSpanDao;
  @Mock private GraphQlSelectionFinder mockSelectionFinder;
  @Mock private ResultSetRequestBuilder mockResultSetRequestBuilder;
  @Mock private FilterRequestBuilder mockFilterRequestBuilder;
  @Mock private DataFetchingFieldSelectionSet mockSelectionSet;
  @Mock private GraphQlRequestContext mockRequestContext;
  @Mock private ResultSetRequest<OrderArgument> mockResultSetRequest;
  @Mock private AttributeAssociation<FilterArgument> mockFilter;
  @Mock private TimeRangeArgument mockTimeRangeArgument;

  private SpanJoinerBuilder spanJoinerBuilder;

  @BeforeEach
  void setup() {
    spanJoinerBuilder =
        new DefaultSpanJoinerBuilder(
            mockSpanDao,
            mockSelectionFinder,
            mockResultSetRequestBuilder,
            mockFilterRequestBuilder);
  }

  @Test
  void fetchSpans() {
    Span span1 = new TestSpan(FIRST_SPAN_ID);
    Span span2 = new TestSpan(SECOND_SPAN_ID);
    TestJoinSource joinSource1 = new TestJoinSource(FIRST_SPAN_ID);
    TestJoinSource joinSource2 = new TestJoinSource(SECOND_SPAN_ID);
    Map<TestJoinSource, Span> expected =
        Map.ofEntries(entry(joinSource1, span1), entry(joinSource2, span2));
    List<TestJoinSource> joinSources = List.of(joinSource1, joinSource2);
    mockRequestedSelectionFields(
        List.of(mock(SelectedField.class), mock(SelectedField.class)), "pathToSpan");
    mockRequestBuilding();
    mockResult(List.of(span1, span2));
    SpanJoiner joiner =
        this.spanJoinerBuilder
            .build(
                this.mockRequestContext,
                this.mockTimeRangeArgument,
                this.mockSelectionSet,
                List.of("pathToSpan"))
            .blockingGet();
    assertEquals(
        expected, joiner.joinSpans(joinSources, new TestJoinSourceIdGetter()).blockingGet());
  }

  private void mockRequestBuilding() {
    when(mockFilterRequestBuilder.build(eq(mockRequestContext), eq(SPAN), anySet()))
        .thenReturn(Single.just(List.of(mockFilter)));

    when(mockResultSetRequestBuilder.build(
            eq(mockRequestContext),
            eq(SPAN),
            eq(2),
            eq(0),
            eq(mockTimeRangeArgument),
            eq(emptyList()),
            eq(List.of(mockFilter)),
            any(Stream.class),
            eq(Optional.empty())))
        .thenReturn(Single.just(mockResultSetRequest));
  }

  private void mockRequestedSelectionFields(List<SelectedField> selectedFields, String location) {
    when(mockSelectionFinder.findSelections(
            mockSelectionSet,
            SelectionQuery.builder().selectionPath(List.of(location, "span")).build()))
        .thenReturn(selectedFields.stream());
  }

  private void mockResult(List<Span> spans) {
    when(mockSpanDao.getSpans(any(SpanRequest.class)))
        .thenAnswer(invocation -> Single.just(new TestSpanResultSet(spans)));
  }

  @Value
  private static class TestJoinSource {
    String spanId;
  }

  private static class TestJoinSourceIdGetter implements SpanIdGetter<TestJoinSource> {
    @Override
    public Single<String> getSpanId(TestJoinSource source) {
      if (source.getSpanId() == null || source.getSpanId().isEmpty()) {
        return Single.error(new IllegalArgumentException("Empty spanId"));
      }
      return Single.just(source.getSpanId());
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class TestSpanResultSet implements SpanResultSet {
    List<Span> results;
    long count = 0;
    long total = 0;
  }

  @Value
  @Accessors(fluent = true)
  private static class TestSpan implements Span {
    String id;

    @Override
    public Object attribute(AttributeExpression expression) {
      return null;
    }

    @Override
    public LogEventResultSet logEvents() {
      return null;
    }
  }
}
