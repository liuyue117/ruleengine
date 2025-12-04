package com.ly;

import com.ly.core.RuleContext;
import com.ly.core.RuleEngine;
import com.ly.impl.ExpressionCondition;
import com.ly.impl.LotteryAction;
import com.ly.impl.LotteryAction.CustomerPurchase;
import com.ly.impl.GenericAction;
import com.ly.model.Rule;
import com.ly.model.RuleBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// 高级规则引擎演示 - 展示新功能
public class AdvancedRuleEngineDemo {
    private static final Random random = new Random();
    
    // 测试数据模型类
    static class User {
        private String userType;
        private double orderTotal;
        private List<Goods> goods;
        
        User(String userType, double orderTotal, List<Goods> goods) {
            this.userType = userType;
            this.orderTotal = orderTotal;
            this.goods = goods;
        }
        
        public String getUserType() { return userType; }
        public double getOrderTotal() { return orderTotal; }
        public List<Goods> getGoods() { return goods; }
    }
    
    static class Goods {
        private String name;
        private double price;
        
        Goods(String name, double price) {
            this.name = name;
            this.price = price;
        }
        
        public String getName() { return name; }
        public double getPrice() { return price; }
    }
    
    public static void main(String[] args) {
        // 1. 初始化规则引擎
        RuleEngine engine = new RuleEngine();
        
        // 3. 定义高级规则
        
        // 规则1: 复杂表达式规则 - 检查用户商品数量和价格
        Rule complexExpressionRule = RuleBuilder.create()
                .id("R1")
                .name("Complex Expression Rule")
                .priority(10)
                .when(new ExpressionCondition("user.goods.size() > 3"))
                .then(new GenericAction("Process Large Order", ctx -> {
                    System.out.println("Processing large order with multiple items!");
                    User user = ctx.get("user", User.class);
                    System.out.println("User has " + user.getGoods().size() + " items in order");
                }))
                .build();
        
        // 规则2: 带生效时间的规则 - 在特定时间范围内生效
        LocalDateTime effectiveTime = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        LocalDateTime expirationTime = LocalDateTime.now().plus(1, ChronoUnit.HOURS);
        
        Rule timeBasedRule = RuleBuilder.create()
                .id("R2")
                .name("Time-based Promotion Rule")
                .priority(8)
                .effectiveTime(effectiveTime)
                .expirationTime(expirationTime)
                .when(new ExpressionCondition("user.orderTotal > 200"))
                .then(new GenericAction("Apply Time-limited Discount", ctx -> {
                    System.out.println("Applying 15% time-limited discount!");
                    User user = ctx.get("user", User.class);
                    double discountedTotal = user.getOrderTotal() * 0.85;
                    ctx.put("discountedTotal", discountedTotal);
                }))
                .build();
        
        // 规则3: 数组元素条件规则 - 检查特定商品价格
        Rule arrayElementRule = RuleBuilder.create()
                .id("R3")
                .name("High-value Item Rule")
                .priority(7)
                .when(new ExpressionCondition("user.goods[0].price > 500"))
                .then(new GenericAction("Handle High-value Item", ctx -> {
                    System.out.println("Order contains high-value item - applying special handling!");
                    User user = ctx.get("user", User.class);
                    Goods firstGoods = user.getGoods().get(0);
                    System.out.println("High-value item: " + firstGoods.getName() + ", Price: " + firstGoods.getPrice());
                }))
                .build();
        
        // 规则4: 抽奖规则 - 每小时抽取3名中奖者
        Rule lotteryRule = RuleBuilder.create()
                .id("R4")
                .name("Hourly Lottery Rule")
                .priority(5)
                .when(ctx -> true) // 始终执行抽奖
                .then(new LotteryAction(
                    "Hourly Customer Lottery",
                    3, // 抽取3名中奖者
                    ChronoUnit.HOURS, // 时间单位为小时
                    1, // 过去1小时内的购买记录
                    "customerPurchases", // 上下文客户列表键
                    "lotteryWinners" // 上下文中奖者结果键
                ))
                .build();
        
        // 注册所有规则
//        engine.registerRule(complexExpressionRule);
        engine.registerRule(timeBasedRule);
        engine.registerRule(arrayElementRule);
        engine.registerRule(lotteryRule);
        
        // 4. 创建测试数据
        
        // 创建用户和商品数据
        List<Goods> goodsList = new ArrayList<>();
        goodsList.add(new Goods("Laptop", 899.99));
        goodsList.add(new Goods("Mouse", 49.99));
        goodsList.add(new Goods("Keyboard", 79.99));
        goodsList.add(new Goods("Monitor", 299.99));
        
        User testUser = new User("VIP", 1329.96, goodsList);
        
        // 创建模拟的客户购买记录
        List<CustomerPurchase> customerPurchases = generateMockCustomerPurchases(20);
        
        // 5. 执行规则引擎
        System.out.println("--- Advanced Rule Engine Demo ---");
        System.out.println("Current Time: " + LocalDateTime.now());
        System.out.println("Time-based Rule Effective: " + effectiveTime);
        System.out.println("Time-based Rule Expiration: " + expirationTime);
        System.out.println();
        
        RuleContext context = new RuleContext();
        context.put("user", testUser);
        context.put("customerPurchases", customerPurchases);
        
        engine.fire(context);
        
        // 显示结果
        System.out.println();
        System.out.println("--- Results ---");
        Double discountedTotal = context.get("discountedTotal", Double.class);
        if (discountedTotal != null) {
            System.out.println("Discounted Total: $" + String.format("%.2f", discountedTotal));
        }
        
        List<CustomerPurchase> winners = context.get("lotteryWinners", List.class);
        if (winners != null && !winners.isEmpty()) {
            System.out.println("Lottery Winners Count: " + winners.size());
        }
    }
    
    /**
     * 生成模拟的客户购买记录
     */
    private static List<CustomerPurchase> generateMockCustomerPurchases(int count) {
        List<CustomerPurchase> purchases = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 1; i <= count; i++) {
            // 在过去2小时内随机生成购买时间
            LocalDateTime purchaseTime = now.minus(random.nextInt(120), ChronoUnit.MINUTES);
            purchases.add(new CustomerPurchase("CUST" + String.format("%03d", i), purchaseTime));
        }
        
        return purchases;
    }
}