package com.ly;

import com.ly.core.RuleEngine;
import com.ly.core.RuleContext;
import com.ly.model.Rule;
import com.ly.impl.TimeCondition;
import com.ly.impl.LogAction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestTimeRule {
    public static void main(String[] args) {
        // 创建规则引擎
        RuleEngine engine = new RuleEngine();
        
        // 创建时间条件（当前时间前后1小时内有效）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusHours(1);
        LocalDateTime endTime = now.plusHours(1);
        
        TimeCondition timeCondition = new TimeCondition(startTime, endTime);
        
        // 创建日志动作
        LogAction logAction = new LogAction("时间规则触发！当前时间在有效范围内");
        
        // 创建规则
        Rule timeRule = new Rule("time_rule_001", "时间规则测试", 1);
        timeRule.setCondition(timeCondition);
        timeRule.addAction(logAction);
        
        // 注册规则
        engine.registerRule(timeRule);
        
        // 创建规则上下文
        RuleContext ctx = new RuleContext();
        
        // 执行规则
        engine.fire(ctx);
        
        // 输出时间信息供验证
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("\n=== 时间规则验证 ===");
        System.out.println("规则生效时间: " + startTime.format(formatter));
        System.out.println("规则过期时间: " + endTime.format(formatter));
        System.out.println("当前时间: " + now.format(formatter));
        System.out.println("===================");
    }
}