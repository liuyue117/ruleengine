# Rule Engine

一个功能强大的Java规则引擎，支持复杂表达式、定时规则和随机抽奖等高级功能。

## 功能特性

### 核心功能
- **规则管理**: 支持规则的注册、删除和优先级排序
- **条件评估**: 基于上下文数据评估规则条件
- **动作执行**: 当规则条件满足时执行相应动作
- **优先级处理**: 支持规则按优先级顺序执行
- **互斥规则**: 支持设置互斥规则，执行后停止后续规则

### 高级功能

#### 1. 复杂表达式支持
- 支持方法调用: `user.goods.size() > 3`
- 支持数组/列表访问: `user.goods[0].price > 500`
- 支持字段访问: `user.orderTotal > 200`
- 支持数值比较: `>`, `<`, `>=`, `<=`, `==`, `!=`
- 支持字符串比较: `==`, `!=`, `contains`

#### 2. 定时规则
- 支持设置规则生效时间
- 支持设置规则过期时间
- 自动在时间范围内评估规则

#### 3. 随机抽奖功能
- 支持按时间段筛选购买客户
- 支持随机抽取指定数量的中奖者
- 支持分钟/小时为单位的时间区间
- 自动记录和返回中奖结果

## 项目结构

```
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── ly/
│                   ├── core/          # 核心接口
│                   │   ├── Action.java
│                   │   ├── Condition.java
│                   │   ├── RuleContext.java
│                   │   └── RuleEngine.java
│                   ├── impl/          # 实现类
│                   │   ├── CompositeCondition.java
│                   │   ├── FieldComparisonCondition.java
│                   │   ├── ExpressionCondition.java
│                   │   ├── GenericAction.java
│                   │   └── LotteryAction.java
│                   ├── model/         # 数据模型
│                   │   ├── Rule.java
│                   │   └── RuleBuilder.java
│                   ├── Main.java      # 基础演示
│                   └── AdvancedRuleEngineDemo.java  # 高级功能演示
├── pom.xml
└── README.md
```

## 快速开始

### 基础使用

```java
// 1. 初始化规则引擎
RuleEngine engine = new RuleEngine();

// 2. 创建规则
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
            double total = ctx.get("orderTotal", Double.class);
            ctx.put("finalPrice", total * 0.8);
        }))
        .build();

// 3. 注册规则
engine.registerRule(vipRule);

// 4. 执行规则
RuleContext context = new RuleContext();
context.put("userType", "VIP");
context.put("orderTotal", 150.0);
engine.fire(context);
```

### 高级功能使用

#### 复杂表达式规则

```java
Rule complexRule = RuleBuilder.create()
        .id("R1")
        .name("Complex Expression Rule")
        .priority(10)
        .when(new ExpressionCondition("user.goods.size() > 3"))
        .then(new GenericAction("Process Large Order", ctx -> {
            // 处理大订单
        }))
        .build();
```

#### 定时规则

```java
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
            // 应用限时折扣
        }))
        .build();
```

#### 抽奖规则

```java
Rule lotteryRule = RuleBuilder.create()
        .id("R4")
        .name("Hourly Lottery Rule")
        .priority(5)
        .when(ctx -> true)
        .then(new LotteryAction(
            "Hourly Customer Lottery",
            3, // 抽取3名中奖者
            ChronoUnit.HOURS, // 时间单位
            1, // 过去1小时
            "customerPurchases", // 客户列表键
            "lotteryWinners" // 中奖者结果键
        ))
        .build();
```

## 运行演示

### 基础演示
```bash
java com.ly.Main
```

### 高级功能演示
```bash
java com.ly.AdvancedRuleEngineDemo
```

## 构建项目

```bash
mvn clean package
```

## 技术栈
- Java 8+
- Maven
- Lambda表达式
- 函数式接口
- Java时间API

## 扩展开发

### 添加新的Condition类型
实现`com.ly.core.Condition`接口并实现`evaluate`方法。

### 添加新的Action类型
实现`com.ly.core.Action`接口并实现`execute`方法。

### 自定义表达式解析
扩展`ExpressionCondition`类以支持更多表达式语法。
