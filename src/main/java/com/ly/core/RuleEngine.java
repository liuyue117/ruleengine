package com.ly.core;

import com.ly.model.Rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The engine that manages and executes rules
 * 管理和执行规则的引擎
 */
public class RuleEngine {
    private final List<Rule> rules = new ArrayList<>();

    public void registerRule(Rule rule) {
        rules.add(rule);
        Collections.sort(rules);  // Keep sorted by priority
    }

    public void clearRules() {
        rules.clear();
    }

    public void fire(RuleContext context) {
        for (Rule rule : rules) {
            if (rule.evaluate(context)) {
                rule.execute(context);
                if (rule.isExclusive()) {
                    break;
                }
            }
        }
    }
}
