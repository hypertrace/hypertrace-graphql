package org.hypertrace.core.graphql.log.event.dao;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.log.event.request.LogEventRequest;
import org.hypertrace.core.graphql.log.event.schema.LogEventResultSet;

public interface LogEventDao {

  Single<LogEventResultSet> getLogEvents(LogEventRequest request);
}
