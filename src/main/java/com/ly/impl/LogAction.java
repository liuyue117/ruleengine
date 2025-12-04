package com.ly.impl;

import com.ly.core.Action;
import com.ly.core.RuleContext;

public class LogAction implements Action {
    private String message;
    
    public LogAction(String message) {
        this.message = message;
    }
    
    @Override
    public void execute(RuleContext context) {
        System.out.println("[LOG] " + message);
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}