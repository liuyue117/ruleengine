package com.ly;

import com.ly.core.RuleContext;
import com.ly.core.RuleEngine;
import com.ly.impl.ExpressionCondition;
import com.ly.impl.LotteryAction;
import com.ly.model.Rule;
import com.ly.model.RuleBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 主测试类
 * 验证所有新功能是否正常工作
 */
public class Main {
    
    public static void main(String[] args) {
        // 测试表达式解析器功能
        testExpressionEvaluator();
        
        // 测试规则生效时间功能
        testRuleEffectiveTime();
        
        // 测试抽奖功能
        testLotteryFunction();
    }
    
    /**
     * 测试表达式解析器功能
     */
    private static void testExpressionEvaluator() {
        System.out.println("\n=== 测试表达式解析器功能 ===");
        
        // 创建RuleContext并添加测试数据
        RuleContext context = new RuleContext();
        
        // 测试简单算术运算作为条件
        ExpressionCondition condition1 = new ExpressionCondition("1 + 2 * 3 > 5");
        boolean result1 = condition1.evaluate(context);
        System.out.println("测试简单算术运算作为条件 '1 + 2 * 3 > 5': " + result1);
        
        // 测试变量和算术运算
        context.put("x", 5);
        context.put("y", 10);
        ExpressionCondition condition2 = new ExpressionCondition("x * 2 + y > 15");
        boolean result2 = condition2.evaluate(context);
        System.out.println("测试变量和算术运算 'x * 2 + y > 15': " + result2);
        
        // 测试字符串操作
        context.put("str", "Hello World");
        ExpressionCondition condition3 = new ExpressionCondition("str.contains('World') && str.length() > 10");
        boolean result3 = condition3.evaluate(context);
        System.out.println("测试字符串操作 'str.contains(\"World\") && str.length() > 10': " + result3);

        // 测试JSON字符串解析为Java对象
        String userJson = "{\"goods\":[{\"type\":\"food\",\"price\":100},{\"type\":\"clothes\",\"price\":500},{\"type\":\"electronics\",\"price\":1000},{\"type\":\"books\",\"price\":50}]};";
        // 移除字符串末尾的分号
        userJson = userJson.replaceAll(";$", "");

        // 使用JsonUtils将JSON字符串解析为Map对象
        Map<String, Object> userMap = com.ly.util.JsonUtils.fromJsonToMap(userJson);

        // 将解析后的Map对象添加到RuleContext中
        context.put("user", userMap);
        
        // 测试嵌套表达式
        ExpressionCondition condition4 = new ExpressionCondition("user.goods.size() > 3");
        boolean result4 = condition4.evaluate(context);
        System.out.println("测试JSON解析和嵌套表达式 'user.goods.size() > 3': " + result4);

    }
    
    /**
     * 测试规则生效时间功能
     */
    private static void testRuleEffectiveTime() {
        System.out.println("\n=== 测试规则生效时间功能 ===");
        
        // 创建RuleEngine
        RuleEngine engine = new RuleEngine();
        
        // 创建一个现在生效的规则
        Date now = new Date();
        Rule rule1 = RuleBuilder.create()
            .id("rule1")
            .name("现在生效的规则")
            .priority(1)
            .effectiveTime(now)
            .when(new ExpressionCondition("true"))
            .then(context -> System.out.println("规则1执行了"))
            .build();
        
        // 创建一个未来生效的规则
        Date future = new Date(now.getTime() + 3600000); // 1小时后
        Rule rule2 = RuleBuilder.create()
            .id("rule2")
            .name("未来生效的规则")
            .priority(2)
            .effectiveTime(future)
            .when(new ExpressionCondition("true"))
            .then(context -> System.out.println("规则2执行了"))
            .build();
        
        // 注册规则
        engine.registerRule(rule1);
        engine.registerRule(rule2);
        
        // 执行规则
        RuleContext context = new RuleContext();
        engine.fire(context);
        
        System.out.println("规则执行完成。只有规则1应该执行，因为规则2在未来生效。");
    }
    
    /**
     * 测试抽奖功能
     */
    private static void testLotteryFunction() {
        System.out.println("\n=== 测试抽奖功能 ===");
        
        // 创建用户列表
        List<User> users = new ArrayList<>();
        Date now = new Date();
        
        for (int i = 1; i <= 10; i++) {
            // 创建10个用户，购买时间分别为现在、5分钟前、10分钟前...45分钟前
            Date purchaseTime = new Date(now.getTime() - (i - 1) * 300000); // 减去(i-1)*5分钟
            users.add(new User("用户" + i, purchaseTime));
        }
        
        // 创建RuleContext并添加用户列表
        RuleContext context = new RuleContext();
        context.put("users", users);
        
        // 创建抽奖规则：抽取过去30分钟内的2名中奖者
        Date startTime = new Date(now.getTime() - 1800000); // 30分钟前
        Date endTime = now;
        
        Rule lotteryRule = RuleBuilder.create()
            .id("lotteryRule")
            .name("抽奖规则")
            .priority(1)
            .when(new ExpressionCondition("true"))
            .then(new LotteryAction("users", startTime, endTime, 2, "winners"))
            .build();
        
        // 创建RuleEngine并注册规则
        RuleEngine engine = new RuleEngine();
        engine.registerRule(lotteryRule);
        
        // 执行规则
        engine.fire(context);
        
        // 从RuleContext中获取中奖者列表并打印
        List<User> winners = (List<User>) context.get("winners");
        System.out.println("中奖者列表：");
        for (User winner : winners) {
            System.out.println(winner.getName() + " (购买时间：" + winner.getPurchaseTime() + ")");
        }
    }
    
    /**
      * 测试用户类
      */
     private static class User implements com.ly.model.Purchaser {
         private final String name;
         private final Date purchaseTime;
         
         public User(String name, Date purchaseTime) {
             this.name = name;
             this.purchaseTime = purchaseTime;
         }
         
         public String getName() { return name; }
         @Override
         public Date getPurchaseTime() { return purchaseTime; }
     }
}
