package com.ly.core;
/**
 * Represents an action to be executed when a rule is triggered.
 * 表示规则触发时要执行的操作
 */
@FunctionalInterface // 函数式接口 它是指只包含一个抽象方法的接口。
public interface Action {
    /**
     * Executes the action using the given context.
     * 使用给定的上下文执行该操作
     * @param context The rule context containing data.
     */
    void execute(RuleContext context);
}
