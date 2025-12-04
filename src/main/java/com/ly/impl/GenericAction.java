package com.ly.impl;

import com.ly.core.Action;
import com.ly.core.RuleContext;

import java.util.function.Consumer;

/**
 * A generic action implementation.
 * 通用操作实现
 */
public class GenericAction implements Action {

    private final String name;
    private final Consumer<RuleContext> actionLogic;

    public GenericAction(String name, Consumer<RuleContext> actionLogic){
        this.name = name;
        this.actionLogic = actionLogic;
    }

    @Override
    public void execute(RuleContext context) {
        // System.out.println("Executing action: " + name);
        actionLogic.accept(context);
    }
}
