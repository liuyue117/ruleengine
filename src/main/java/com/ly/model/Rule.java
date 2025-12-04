package com.ly.model;

import com.ly.core.Action;
import com.ly.core.Condition;
import com.ly.core.RuleContext;

import java.time.LocalDateTime;
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
    private LocalDateTime effectiveTime; // Rule effective start time
    private LocalDateTime expirationTime; // Rule expiration time

    public Rule(String id, String name, int priority) {
        this.id = id;
        this.name = name;
        this.priority = priority;
    }

    public boolean evaluate(RuleContext context) {
        // Check if rule is within effective time range
        LocalDateTime now = LocalDateTime.now();
        if (effectiveTime != null && now.isBefore(effectiveTime)) {
            return false;
        }
        if (expirationTime != null && now.isAfter(expirationTime)) {
            return false;
        }
        
        if (condition == null) {
            return true; // Default to true if no condition
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
    public LocalDateTime getEffectiveTime() { return effectiveTime; }
    public void setEffectiveTime(LocalDateTime effectiveTime) { this.effectiveTime = effectiveTime; }
    public LocalDateTime getExpirationTime() { return expirationTime; }
    public void setExpirationTime(LocalDateTime expirationTime) { this.expirationTime = expirationTime; }

    @Override
    public int compareTo(Rule other) {
        // Descending order of priority 优先级降序排序
        return Integer.compare(other.priority, this.priority);
    }
}

