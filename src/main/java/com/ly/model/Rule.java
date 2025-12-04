package com.ly.model;

import com.ly.core.Action;
import com.ly.core.Condition;
import com.ly.core.RuleContext;

import java.util.ArrayList;
import java.util.Date;
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
    
    private Date effectiveTime; // 规则生效时间
    private Date expirationTime; // 规则过期时间

    public Rule(String id, String name, int priority) {
        this.id = id;
        this.name = name;
        this.priority = priority;
    }
    
    public Rule(String id, String name, int priority, Date effectiveTime, Date expirationTime) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.effectiveTime = effectiveTime;
        this.expirationTime = expirationTime;
    }

    public boolean evaluate(RuleContext context) {
        // 检查规则是否在生效时间范围内
        if (!isEffectiveNow()) {
            return false;
        }
        
        if (condition == null) {
            return true;// Default to true if no condition
        }
        return condition.evaluate(context);
    }
    
    /**
     * 检查规则是否在当前时间生效
     * @return true如果规则在当前时间生效，否则false
     */
    private boolean isEffectiveNow() {
        Date now = new Date();
        
        // 如果没有设置生效时间和过期时间，则规则始终生效
        if (effectiveTime == null && expirationTime == null) {
            return true;
        }
        
        // 检查是否在生效时间之后
        boolean afterEffective = (effectiveTime == null) || (now.after(effectiveTime) || now.equals(effectiveTime));
        
        // 检查是否在过期时间之前
        boolean beforeExpiration = (expirationTime == null) || (now.before(expirationTime) || now.equals(expirationTime));
        
        return afterEffective && beforeExpiration;
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
    
    public Date getEffectiveTime() { return effectiveTime; }
    public void setEffectiveTime(Date effectiveTime) { this.effectiveTime = effectiveTime; }
    public Date getExpirationTime() { return expirationTime; }
    public void setExpirationTime(Date expirationTime) { this.expirationTime = expirationTime; }

    @Override
    public int compareTo(Rule other) {
        // Descending order of priority 优先级降序排序
        return Integer.compare(other.priority, this.priority);
    }
}

