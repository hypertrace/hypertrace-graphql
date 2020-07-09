package org.hypertrace.core.graphql.context;

import graphql.kickstart.servlet.context.GraphQLServletContextBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface GraphQlRequestContextBuilder extends GraphQLServletContextBuilder {
  @Override
  GraphQlRequestContext build(
      HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
