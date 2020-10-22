package org.hypertrace.core.graphql.common.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import org.hypertrace.core.graphql.attributes.AttributeModel;
import org.hypertrace.core.graphql.attributes.AttributeStore;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeScope;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterArgument;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterOperatorType;
import org.hypertrace.core.graphql.common.schema.results.arguments.filter.FilterType;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeAssociator;
import org.hypertrace.core.graphql.common.utils.attributes.AttributeScopeStringTranslator;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultFilterRequestBuilderTest {
  @Mock AttributeAssociator mockAttributeAssociator;
  @Mock AttributeStore mockAttributeStore;
  @Mock GraphQlRequestContext mockRequestContext;
  @Mock AttributeScopeStringTranslator mockScopeStringTranslator;

  private DefaultFilterRequestBuilder requestBuilder;

  @BeforeEach
  void beforeEach() {
    this.requestBuilder =
        new DefaultFilterRequestBuilder(
            mockAttributeAssociator, mockAttributeStore, mockScopeStringTranslator);
  }

  @Test
  void canBuildIdFilterWithEnumScope() {
    final FilterOperatorType filterOperatorType = FilterOperatorType.LESS_THAN;
    final long filterValue = 42;
    final FilterType filterType = FilterType.ID;
    final String filterAttributeKey = "filterKey";
    final String idScopeString = "foreign_scope";
    final String currentScope = "primary_scope";

    final AttributeScope idScope = mock(AttributeScope.class);
    when(idScope.getScopeString()).thenReturn(idScopeString);
    when(this.mockScopeStringTranslator.fromExternal(idScopeString)).thenReturn(idScopeString);
    final FilterArgument filterArgument = mock(FilterArgument.class);
    when(filterArgument.type()).thenReturn(filterType);
    when(filterArgument.operator()).thenReturn(filterOperatorType);
    when(filterArgument.value()).thenReturn(filterValue);
    when(filterArgument.idType()).thenReturn(idScope);

    final AttributeModel mockForeignAttribute = mock(AttributeModel.class);
    when(mockForeignAttribute.key()).thenReturn(filterAttributeKey);

    when(this.mockAttributeStore.getForeignIdAttribute(
            mockRequestContext, currentScope, idScopeString))
        .thenReturn(Single.just(mockForeignAttribute));

    AttributeAssociation<FilterArgument> builtFilter =
        this.requestBuilder
            .build(this.mockRequestContext, currentScope, List.of(filterArgument))
            .blockingGet()
            .get(0);

    assertEquals(mockForeignAttribute, builtFilter.attribute());
    assertEquals(filterAttributeKey, builtFilter.value().key());
    assertEquals(filterOperatorType, builtFilter.value().operator());
    assertEquals(filterValue, builtFilter.value().value());
    assertEquals(FilterType.ATTRIBUTE, builtFilter.value().type());
  }

  @Test
  void canBuildIdFilterWithStringScope() {
    final FilterOperatorType filterOperatorType = FilterOperatorType.LESS_THAN;
    final long filterValue = 42;
    final FilterType filterType = FilterType.ID;
    final String filterAttributeKey = "filterKey";
    final String idScope = "foreign_scope";
    final String idScopeExternal = "foreign_scope_external";
    final String currentScope = "primary_scope";

    final FilterArgument filterArgument = mock(FilterArgument.class);
    when(filterArgument.type()).thenReturn(filterType);
    when(filterArgument.operator()).thenReturn(filterOperatorType);
    when(filterArgument.value()).thenReturn(filterValue);
    when(filterArgument.idScope()).thenReturn(idScopeExternal);

    final AttributeModel mockForeignAttribute = mock(AttributeModel.class);
    when(mockForeignAttribute.key()).thenReturn(filterAttributeKey);

    when(this.mockScopeStringTranslator.fromExternal(idScopeExternal)).thenReturn(idScope);
    when(this.mockAttributeStore.getForeignIdAttribute(mockRequestContext, currentScope, idScope))
        .thenReturn(Single.just(mockForeignAttribute));

    AttributeAssociation<FilterArgument> builtFilter =
        this.requestBuilder
            .build(this.mockRequestContext, currentScope, List.of(filterArgument))
            .blockingGet()
            .get(0);

    assertEquals(mockForeignAttribute, builtFilter.attribute());
    assertEquals(filterAttributeKey, builtFilter.value().key());
    assertEquals(filterOperatorType, builtFilter.value().operator());
    assertEquals(filterValue, builtFilter.value().value());
    assertEquals(FilterType.ATTRIBUTE, builtFilter.value().type());
  }
}
