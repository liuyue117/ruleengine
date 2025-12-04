package com.ly.test;

import com.ly.core.RuleContext;
import com.ly.expression.ExpressionEvaluator;
import com.ly.expression.SimpleExpressionEvaluator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 嵌套表达式测试类
 * 验证表达式解析器是否能处理包含层级的规则对比，例如 user.goods.size() > 3
 */
public class NestedExpressionTest {
    
    public static void main(String[] args) {
        // 创建表达式求值器
        ExpressionEvaluator evaluator = new SimpleExpressionEvaluator();
        
        // 创建规则上下文
        RuleContext context = new RuleContext();
        
        // 创建测试用户对象
        User user = new User();
        user.setName("张三");
        
        // 创建测试商品列表
        List<Goods> goodsList = new ArrayList<>();
        goodsList.add(new Goods("商品1", 100));
        goodsList.add(new Goods("商品2", 200));
        goodsList.add(new Goods("商品3", 300));
        goodsList.add(new Goods("商品4", 400));
        
        // 将商品列表设置到用户对象中
        user.setGoods(goodsList);
        
        // 将用户对象添加到规则上下文中
        context.put("user", user);
        
        // 测试嵌套表达式
        try {
            // 测试表达式：user.goods.size() > 3
            String expression = "user.goods.size() > 3";
            Object result = evaluator.evaluate(expression, context);
            System.out.println("测试表达式 '" + expression + "' 结果：" + result);
            
            // 测试表达式：user.goods.size() == 4
            expression = "user.goods.size() == 4";
            result = evaluator.evaluate(expression, context);
            System.out.println("测试表达式 '" + expression + "' 结果：" + result);
            
            // 测试表达式：user.goods.get(0).price < 200
            expression = "user.goods.get(0).price < 200";
            result = evaluator.evaluate(expression, context);
            System.out.println("测试表达式 '" + expression + "' 结果：" + result);
            
            // 测试表达式：user.goods.get(1).name.equals('商品2')
            expression = "user.goods.get(1).name.equals('商品2')";
            result = evaluator.evaluate(expression, context);
            System.out.println("测试表达式 '" + expression + "' 结果：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 测试用户类
     */
    private static class User {
        private String name;
        private List<Goods> goods;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public List<Goods> getGoods() {
            return goods;
        }
        
        public void setGoods(List<Goods> goods) {
            this.goods = goods;
        }
    }
    
    /**
     * 测试商品类
     */
    private static class Goods {
        private String name;
        private int price;
        
        public Goods(String name, int price) {
            this.name = name;
            this.price = price;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public int getPrice() {
            return price;
        }
        
        public void setPrice(int price) {
            this.price = price;
        }
    }
}