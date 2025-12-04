package com.ly.model;

import com.ly.core.Action;
import com.ly.core.Condition;
import com.ly.core.RuleContext;

import java.util.ArrayList;
import java.util.List;

public class Rule implements Comparable<Rule> {
    private String id;
    private String name;
    private String description;
    private int priority; // Higher number = higher priority
    private Condition condition;
    private List<Action> actions = new ArrayList<>();
    private boolean exclusive; // If true, stops execution of subsequent rules if this one matches
    // 互斥 true 则停止执行后续操作，如果与以下条件匹配

    public Rule(String id, String name, int priority) {
        this.id = id;
        this.name = name;
        this.priority = priority;
    }

    public boolean evaluate(RuleContext context) {
        if (condition == null) {
            return true;// Default to true if no condition
        }
        return condition.evaluate(context);
    }

    public void execute(RuleContext context) {
        for (Action action : actions) {
            action.execute(context);
        }
    }

    public void addAction(Action action) {
        this.actions.add(action);
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getPriority() {return priority;}
    public void setCondition(Condition condition) { this.condition = condition;}
    public boolean isExclusive() {return exclusive;}
    public void setExclusive(boolean exclusive) { this.exclusive = exclusive;}

    @Override
    public int compareTo(Rule other) {
        // Descending order of priority 优先级降序排序
        return Integer.compare(other.priority, this.priority);
    }
}

