package org.hypertrace.core.graphql.span.joiner;

import static java.util.Collections.emptyList;
import static java.util.Map.entry;
import static org.hypertrace.core.graphql.atttributes.scopes.HypertraceCoreAttributeScopeString.SPAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.Collections;
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
import org.hypertrace.core.graphql.span.joiner.SpanJoiner.MultipleSpanIdGetter;
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

  private static final String SPAN_ID1 = "spanId1";
  private static final String SPAN_ID2 = "spanId2";
  private static final String SPAN_ID3 = "spanId3";
  private static final String SPAN_ID4 = "spanId4";

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
    Span span1 = new TestSpan(SPAN_ID1);
    Span span2 = new TestSpan(SPAN_ID2);
    TestJoinSource joinSource1 = new TestJoinSource(SPAN_ID1);
    TestJoinSource joinSource2 = new TestJoinSource(SPAN_ID2);
    Map<TestJoinSource, Span> expected =
        Map.ofEntries(entry(joinSource1, span1), entry(joinSource2, span2));
    List<TestJoinSource> joinSources = List.of(joinSource1, joinSource2);
    when(mockSelectionFinder.findSelections(
            mockSelectionSet,
            SelectionQuery.builder().selectionPath(List.of("pathToSpan", "span")).build()))
        .thenReturn(Stream.of(mock(SelectedField.class), mock(SelectedField.class)));
    when(mockFilterRequestBuilder.build(eq(mockRequestContext), eq(SPAN), anyList()))
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
        expected,
        joiner
            .joinSpan(joinSources, new TestJoinSourceIdGetter(), Collections.emptyList())
            .blockingGet());
  }

  @Test
  void fetchMultipleSpans() {
    Span span1 = new TestSpan(SPAN_ID1);
    Span span2 = new TestSpan(SPAN_ID2);
    TestMultipleJoinSource joinSource1 = new TestMultipleJoinSource(List.of(SPAN_ID1, SPAN_ID2));
    TestMultipleJoinSource joinSource2 = new TestMultipleJoinSource(List.of(SPAN_ID3, SPAN_ID4));
    ListMultimap<TestMultipleJoinSource, Span> expected = ArrayListMultimap.create();
    expected.put(joinSource1, span1);
    expected.put(joinSource1, span2);
    List<TestMultipleJoinSource> joinSources = List.of(joinSource1, joinSource2);
    when(mockSelectionFinder.findSelections(
            mockSelectionSet,
            SelectionQuery.builder().selectionPath(List.of("pathToSpans", "spans")).build()))
        .thenReturn(Stream.of(mock(SelectedField.class), mock(SelectedField.class)));
    when(mockFilterRequestBuilder.build(eq(mockRequestContext), eq(SPAN), anyList()))
        .thenReturn(Single.just(List.of(mockFilter)));

    when(mockResultSetRequestBuilder.build(
            eq(mockRequestContext),
            eq(SPAN),
            eq(4),
            eq(0),
            eq(mockTimeRangeArgument),
            eq(emptyList()),
            eq(List.of(mockFilter)),
            any(Stream.class),
            eq(Optional.empty())))
        .thenReturn(Single.just(mockResultSetRequest));

    mockResult(List.of(span1, span2));
    SpanJoiner joiner =
        this.spanJoinerBuilder
            .build(
                this.mockRequestContext,
                this.mockTimeRangeArgument,
                this.mockSelectionSet,
                List.of("pathToSpans"))
            .blockingGet();
    assertEquals(
        expected,
        joiner
            .joinSpans(joinSources, new TestMultipleJoinSourceIdGetter(), Collections.emptyList())
            .blockingGet());
  }

  private void mockResult(List<Span> spans) {
    when(mockSpanDao.getSpans(any(SpanRequest.class)))
        .thenReturn(Single.just(new TestSpanResultSet(spans)));
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

  @Value
  private static class TestMultipleJoinSource {
    List<String> spanIds;
  }

  private static class TestMultipleJoinSourceIdGetter
      implements MultipleSpanIdGetter<TestMultipleJoinSource> {
    @Override
    public Single<List<String>> getSpanIds(TestMultipleJoinSource source) {
      return Single.just(source.getSpanIds());
    }
  }
}
