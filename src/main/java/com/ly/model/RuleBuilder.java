package com.ly.model;

import com.ly.core.Action;
import com.ly.core.Condition;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating Rule instances with a fluent API.
 * 用于使用流畅 API 创建规则实例的构建器。
 */
public class RuleBuilder {
    private String id;
    private String name;
    private int priority;
    private Condition condition;
    private List<Action> actions = new ArrayList<>();
    private boolean exclusive = false;

    private RuleBuilder() {}

    public static RuleBuilder create() {
        return new RuleBuilder();
    }

    public RuleBuilder id(String id) {
        this.id = id;
        return this;
    }

    public RuleBuilder name(String name) {
        this.name = name;
        return this;
    }

    public RuleBuilder priority(int priority){
        this.priority = priority;
        return this;
    }

    public RuleBuilder when(Condition condition){
        this.condition = condition;
        return this;
    }

    public RuleBuilder then(Action action){
        this.actions.add(action);
        return this;
    }

    public RuleBuilder exclusive() {
        this.exclusive = true;
        return this;
    }

    public Rule build() {
        if (id == null) {
            throw new IllegalStateException("Rule ID is required");
        }
        Rule rule = new Rule(id, name != null? name:id, priority);
        rule.setCondition(condition);
        rule.setExclusive(exclusive);
        for (Action action : actions) {
            rule.addAction(action);
        }
        return rule;
    }
}
