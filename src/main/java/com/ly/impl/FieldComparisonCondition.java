package com.ly.impl;

import com.ly.core.Condition;
import com.ly.core.RuleContext;

/**
 * A condition that compares a field in the context with a value.
 * 将上下文中的字段与某个值进行比较的条件。
 */
public class FieldComparisonCondition implements Condition {
    public enum Operator {
        EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, CONTAINS
    }

    private final String fieldName;
    private final Operator operator;
    private final Object value;

    public FieldComparisonCondition(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.value = value;
    }
    @Override
    public boolean evaluate(RuleContext context) {
        Object contextValue = context.get(fieldName);

        if (contextValue == null) {
            return value == null && operator == Operator.EQUALS;
        }

        switch (operator) {
            case EQUALS:
                return contextValue.equals(value);
            case NOT_EQUALS:
                return !contextValue.equals(value);
            case GREATER_THAN:
                if (contextValue instanceof Number && value instanceof Number) {
                    return ((Number) contextValue).doubleValue() > ((Number) value).doubleValue();
                }
                return false;
            case LESS_THAN:
                if (contextValue instanceof Number && value instanceof Number) {
                    return ((Number) contextValue).doubleValue() < ((Number) value).doubleValue();
                }
                return false;
            case CONTAINS:
                if (contextValue instanceof String && value instanceof String) {
                    return ((String) contextValue).contains((String) value);
                }
                return false;
            default:
                return false;
        }
    }
}
