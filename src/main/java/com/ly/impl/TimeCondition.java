package com.ly.impl;

import com.ly.core.Condition;
import com.ly.core.RuleContext;

import java.time.LocalDateTime;

public class TimeCondition implements Condition {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    public TimeCondition(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    @Override
    public boolean evaluate(RuleContext context) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}