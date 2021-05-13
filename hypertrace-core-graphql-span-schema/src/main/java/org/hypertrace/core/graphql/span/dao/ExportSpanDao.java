package org.hypertrace.core.graphql.span.dao;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.span.export.ExportSpan;
import org.hypertrace.core.graphql.span.export.ExportSpanConverter;
import org.hypertrace.core.graphql.span.request.SpanRequest;
import org.hypertrace.core.graphql.span.schema.ExportSpanResult;
import org.hypertrace.core.graphql.span.schema.SpanResultSet;

public class ExportSpanDao {
  private final SpanDao spanDao;

  @Inject
  ExportSpanDao(SpanDao spanDao) {
    this.spanDao = spanDao;
  }

  public Single<ExportSpanResult> getSpans(SpanRequest request) {
    return this.spanDao
        .getSpans(request)
        .flatMap(spanResultSet -> this.buildResponse(spanResultSet));
  }

  private Single<ExportSpanResult> buildResponse(SpanResultSet result) throws Exception {
    List<ExportSpan> exportSpans =
        result.results().stream()
            .map(span -> new ExportSpan.Builder(span).build())
            .collect(Collectors.toList());
    return Single.just(new ExportSpanResultImpl(ExportSpanConverter.toJson(exportSpans)));
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ExportSpanResultImpl implements ExportSpanResult {
    String result;
  }
}
