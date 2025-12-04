package com.ly.impl;

import com.ly.core.Condition;
import com.ly.core.RuleContext;
import com.ly.expression.ExpressionEvaluator;
import com.ly.expression.SimpleExpressionEvaluator;

/**
 * 基于表达式的条件实现
 * 支持使用复杂表达式作为规则条件
 */
public class ExpressionCondition implements Condition {
    
    private final String expression;
    private final ExpressionEvaluator evaluator;
    
    /**
     * 构造函数
     * @param expression 条件表达式
     */
    public ExpressionCondition(String expression) {
        this.expression = expression;
        this.evaluator = new SimpleExpressionEvaluator();
    }
    
    /**
     * 构造函数
     * @param expression 条件表达式
     * @param evaluator 表达式求值器
     */
    public ExpressionCondition(String expression, ExpressionEvaluator evaluator) {
        this.expression = expression;
        this.evaluator = evaluator;
    }
    
    @Override
    public boolean evaluate(RuleContext context) {
        try {
            Object result = evaluator.evaluate(expression, context);
            if (result instanceof Boolean) {
                return (Boolean) result;
            } else {
                throw new RuntimeException("表达式求值结果不是布尔值: " + expression);
            }
        } catch (Exception e) {
            throw new RuntimeException("表达式条件求值失败: " + expression, e);
        }
    }
    
    public String getExpression() {
        return expression;
    }
}
