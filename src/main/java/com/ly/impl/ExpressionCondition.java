package com.ly.impl;

import com.ly.core.Condition;
import com.ly.core.RuleContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A condition that evaluates complex expressions including method calls and array operations.
 * 支持复杂表达式评估的条件，包括方法调用和数组操作
 */
public class ExpressionCondition implements Condition {
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("([\\w\\.\\[\\]()]+)\\s*([<>=!]+)\\s*([\\w\\.\\[\\]()]+)");
    private static final Pattern METHOD_CALL_PATTERN = Pattern.compile("(\\w+)\\.([\\w]+)\\(\\)");
    private static final Pattern ARRAY_ACCESS_PATTERN = Pattern.compile("(\\w+)\\[(\\d+)\\]\\.(\\w+)");
    private static final Pattern FIELD_ACCESS_PATTERN = Pattern.compile("(\\w+)\\.(\\w+)");
    
    private final String expression;
    
    public ExpressionCondition(String expression) {
        this.expression = expression;
    }
    
    @Override
    public boolean evaluate(RuleContext context) {
        Matcher matcher = EXPRESSION_PATTERN.matcher(expression);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid expression format: " + expression);
        }
        
        String leftOperand = matcher.group(1);
        String operator = matcher.group(2);
        String rightOperand = matcher.group(3);
        
        Object leftValue = evaluateOperand(leftOperand, context);
        Object rightValue = evaluateOperand(rightOperand, context);
        
        // 添加调试信息
        System.out.println("Evaluating expression: " + expression);
        System.out.println("Left operand: " + leftOperand + " = " + leftValue + " (type: " + (leftValue != null ? leftValue.getClass() : "null") + ")");
        System.out.println("Right operand: " + rightOperand + " = " + rightValue + " (type: " + (rightValue != null ? rightValue.getClass() : "null") + ")");
        
        return compareValues(leftValue, operator, rightValue);
    }
    
    private Object evaluateOperand(String operand, RuleContext context) {
        // Check if it's a method call like "user.goods.size()"
        Matcher methodMatcher = METHOD_CALL_PATTERN.matcher(operand);
        if (methodMatcher.matches()) {
            String fieldName = methodMatcher.group(1);
            String methodName = methodMatcher.group(2);
            return invokeMethod(context, fieldName, methodName);
        }
        
        // Check if it's an array access like "user.goods[i].price"
        Matcher arrayMatcher = ARRAY_ACCESS_PATTERN.matcher(operand);
        if (arrayMatcher.matches()) {
            String fieldName = arrayMatcher.group(1);
            int index = Integer.parseInt(arrayMatcher.group(2));
            String propertyName = arrayMatcher.group(3);
            return getArrayProperty(context, fieldName, index, propertyName);
        }
        
        // Check if it's a field access like "user.age"
        Matcher fieldMatcher = FIELD_ACCESS_PATTERN.matcher(operand);
        if (fieldMatcher.matches()) {
            String objectName = fieldMatcher.group(1);
            String propertyName = fieldMatcher.group(2);
            return getObjectProperty(context, objectName, propertyName);
        }
        
        // Check if it's a direct field in context
        Object value = context.get(operand);
        if (value != null) {
            return value;
        }
        
        // Try to parse as number
        try {
            // 先尝试解析为整数，避免浮点数精度问题（如100 vs 100.0）
            if (!operand.contains(".") && !operand.contains("e") && !operand.contains("E")) {
                return Integer.parseInt(operand);
            }
            // 再尝试解析为双精度浮点数
            return Double.parseDouble(operand);
        } catch (NumberFormatException e) {
            // If not a number, return as string
            return operand;
        }
    }
    
    private Object invokeMethod(RuleContext context, String fieldName, String methodName) {
        Object obj = context.get(fieldName);
        if (obj == null) {
            throw new IllegalArgumentException("Field not found: " + fieldName);
        }
        
        try {
            Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method not found: " + methodName + " on " + obj.getClass().getName());
        } catch (Exception e) {
            throw new RuntimeException("Error invoking method: " + methodName, e);
        }
    }
    
    private Object getArrayProperty(RuleContext context, String fieldName, int index, String propertyName) {
        Object obj = context.get(fieldName);
        if (obj == null) {
            throw new IllegalArgumentException("Field not found: " + fieldName);
        }
        
        if (!(obj instanceof List)) {
            throw new IllegalArgumentException("Field is not a List: " + fieldName);
        }
        
        List<?> list = (List<?>) obj;
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + list.size());
        }
        
        Object element = list.get(index);
        return getObjectProperty(element, propertyName);
    }
    
    private Object getObjectProperty(RuleContext context, String objectName, String propertyName) {
        Object obj = context.get(objectName);
        if (obj == null) {
            throw new IllegalArgumentException("Object not found: " + objectName);
        }
        
        return getObjectProperty(obj, propertyName);
    }
    
    private Object getObjectProperty(Object obj, String propertyName) {
        try {
            String methodName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Property not found: " + propertyName + " on " + obj.getClass().getName());
        } catch (Exception e) {
            throw new RuntimeException("Error accessing property: " + propertyName, e);
        }
    }
    
    private boolean compareValues(Object left, String operator, Object right) {
        if (left == null || right == null) {
            return false;
        }
        
        if (left instanceof Number && right instanceof Number) {
            double leftNum = ((Number) left).doubleValue();
            double rightNum = ((Number) right).doubleValue();
            
            switch (operator) {
                case ">": return leftNum > rightNum;
                case "<": return leftNum < rightNum;
                case ">=": return leftNum >= rightNum;
                case "<=": return leftNum <= rightNum;
                case "==": return leftNum == rightNum;
                case "!=": return leftNum != rightNum;
                default: throw new IllegalArgumentException("Unsupported operator: " + operator);
            }
        }
        
        if (left instanceof String && right instanceof String) {
            String leftStr = (String) left;
            String rightStr = (String) right;
            
            switch (operator) {
                case "==": return leftStr.equals(rightStr);
                case "!=": return !leftStr.equals(rightStr);
                case "contains": return leftStr.contains(rightStr);
                default: throw new IllegalArgumentException("Unsupported operator for strings: " + operator);
            }
        }
        
        throw new IllegalArgumentException("Unsupported types for comparison: " + left.getClass() + " and " + right.getClass());
    }
}