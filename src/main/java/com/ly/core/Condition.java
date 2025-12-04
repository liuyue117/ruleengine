package com.ly.core;

/**
 * Represents a condition that must be met for a rule to trigger.
 * 表示规则触发必须满足的条件
 */
@FunctionalInterface
public interface Condition {

    /**
     * Evaluates the condition against the given context.
     * 根据给定的上下文评估该条件
     * @param context The rule context containing data.包含数据的规则上下文
     * @return true if the condition is met, false otherwise.
     */
    boolean evaluate(RuleContext context);
}
