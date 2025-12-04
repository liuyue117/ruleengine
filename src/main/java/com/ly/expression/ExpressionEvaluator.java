package com.ly.expression;

import com.ly.core.RuleContext;

/**
 * 表达式求值器接口
 * 支持函数调用、数组索引和算术运算
 */
public interface ExpressionEvaluator {
    
    /**
     * 解析并计算表达式的值
     * @param expression 表达式字符串
     * @param context 规则上下文
     * @return 表达式的计算结果
     */
    Object evaluate(String expression, RuleContext context);
}
