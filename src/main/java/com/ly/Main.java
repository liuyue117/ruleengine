package com.ly;

import com.ly.core.RuleContext;
import com.ly.core.RuleEngine;
import com.ly.impl.FieldComparisonCondition;
import com.ly.impl.GenericAction;
import com.ly.model.Rule;
import com.ly.model.RuleBuilder;

import static com.ly.impl.CompositeCondition.and;
import static com.ly.impl.FieldComparisonCondition.Operator.EQUALS;
import static com.ly.impl.FieldComparisonCondition.Operator.GREATER_THAN;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // 1.Initialize Engine
        RuleEngine engine = new RuleEngine();

        // 2. Define Rules using Builder Pattern

        // Rule 1: VIP Discount (Priority 10, Exclusive)
        Rule vipRule = RuleBuilder.create()
                .id("R1")
                .name("VIP Discount")
                .priority(10)
                .exclusive()
                .when(and(
                        new FieldComparisonCondition("userType", EQUALS, "VIP"),
                        new FieldComparisonCondition("orderTotal", GREATER_THAN, 100.0)
                ))
                .then(new GenericAction("Apply 20% Discount", ctx -> {
                    System.out.println("Applying VIP 20% discount!");
                    double total = ctx.get("orderTotal", Double.class);
                    ctx.put("finalPrice", total * 0.8);
                }))
                .build();

        // Rule 2: Big Spender Discount (Priority 5)
        Rule bigSpenderRule = RuleBuilder.create()
                .id("R2")
                .name("Big Spender Discount")
                .priority(5)
                .when(new FieldComparisonCondition("orderTotal", GREATER_THAN, 200.0))
                .then(new GenericAction("Apply 10% Discount", ctx -> {
                    System.out.println("Applying Big Spender 10% discount!");
                    double total = ctx.get("orderTotal", Double.class);
                    ctx.put("finalPrice",  total * 0.9);
                }))
                .build();

        // Register Rules
        engine.registerRule(vipRule);
        engine.registerRule(bigSpenderRule);

        // 3. Test Scenarios
        System.out.println("--- Scenario 1: VIP User with $150 Order ---");
        RuleContext ctx1 = new RuleContext();
        ctx1.put("userType", "VIP");
        ctx1.put("orderTotal", 150.0);

        engine.fire(ctx1);
        System.out.println("Final Price: " + ctx1.get("finalPrice"));

        System.out.println("\n--- Scenario 2: Regular User with $250 Order ---");
        RuleContext ctx2 = new RuleContext();
        ctx2.put("userType", "REGULAR");
        ctx2.put("orderTotal", 250.0);

        engine.fire(ctx2);
        System.out.println("Final Price: " + ctx2.get("finalPrice"));

        System.out.println("\n--- Scenario 3: VIP User with $50 Order ---");
        RuleContext ctx3 = new RuleContext();
        ctx3.put("userType", "VIP");
        ctx3.put("orderTotal", 50.0);

        engine.fire(ctx3);
        System.out.println("Final Price: " + ctx3.get("finalPrice"));
    }
}