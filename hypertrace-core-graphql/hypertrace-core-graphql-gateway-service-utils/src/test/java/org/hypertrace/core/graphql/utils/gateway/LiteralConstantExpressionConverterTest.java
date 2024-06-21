package org.hypertrace.core.graphql.utils.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.gateway.service.v1.common.Expression;
import org.hypertrace.gateway.service.v1.common.LiteralConstant;
import org.hypertrace.gateway.service.v1.common.Value;
import org.hypertrace.gateway.service.v1.common.ValueType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LiteralConstantExpressionConverterTest {
  @Mock LiteralConstantConverter mockLiteralConstantConverter;

  @Test
  void convertsLiteralExpression() {
    LiteralConstant expectedLiteral =
        LiteralConstant.newBuilder()
            .setValue(Value.newBuilder().setString("foo").setValueType(ValueType.STRING))
            .build();

    LiteralConstantExpressionConverter columnIdentifierExpressionConverter =
        new LiteralConstantExpressionConverter(this.mockLiteralConstantConverter);

    when(this.mockLiteralConstantConverter.convert(eq("foo")))
        .thenReturn(Single.just(expectedLiteral));

    assertEquals(
        Expression.newBuilder().setLiteral(expectedLiteral).build(),
        columnIdentifierExpressionConverter.convert("foo").blockingGet());
  }
}
