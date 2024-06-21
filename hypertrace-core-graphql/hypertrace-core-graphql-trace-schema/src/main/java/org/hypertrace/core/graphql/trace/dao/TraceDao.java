package org.hypertrace.core.graphql.trace.dao;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.trace.request.TraceRequest;
import org.hypertrace.core.graphql.trace.schema.TraceResultSet;

public interface TraceDao {

  Single<TraceResultSet> getTraces(TraceRequest request);
}
