package com.ly.impl;

import com.ly.core.Condition;
import com.ly.core.RuleContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A condition that combines other conditions using logical operators.
 * 复合条件 使用逻辑运算符将其他条件组合在一起的条件。
 */
public class CompositeCondition implements Condition {
    public enum Operator{
        AND, OR, NOT
    }

    private final Operator operator;
    private final List<Condition> conditions = new ArrayList<>();

    public CompositeCondition(Operator operator, Condition... conditions) {
        this.operator = operator;
        this.conditions.addAll(Arrays.asList(conditions));
    }

    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    @Override
    public boolean evaluate(RuleContext context) {
        if (conditions.isEmpty()) {
            return true;
        }
        switch (operator) {
            case AND: // 只要遇到任意一个条件返回 false，整个表达式立即返回 false。 只有当所有条件都为 true 时，才返回 true。
                for (Condition c : conditions) {
                    if (!c.evaluate(context)) return false;
                }
                return true;
            case OR: // 只要遇到任意一个条件返回 true，整个表达式立即返回 true。只有当所有条件都为 false 时，才返回 false。
                for (Condition c : conditions) {
                    if (c.evaluate(context)) return true;
                }
                return false;
            case NOT: // 取列表中的第一个条件（通常 NOT 只包含一个子条件）。 计算该条件的结果，然后取反（!）返回。
                // NOT expects exactly one condition
                return !conditions.get(0).evaluate(context);
            default:
                return false;
        }
    }

    public static CompositeCondition and(Condition... conditions) {
        return new CompositeCondition(Operator.AND, conditions);
    }

    public static CompositeCondition or(Condition... conditions) {
        return new CompositeCondition(Operator.OR, conditions);
    }

    public static CompositeCondition not(Condition... conditions) {
        return new CompositeCondition(Operator.NOT, conditions);
    }
}
