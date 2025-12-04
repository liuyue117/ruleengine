package com.ly.impl;

import com.ly.core.Condition;
import com.ly.core.RuleContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A condition that evaluates complex expressions using reflection.
 * Supports: 
 * - Collection operations (size(), isEmpty())
 * - Nested property access (user.goods[i].price)
 * - Method calls (user.getAge(), goods.size())
 * - Arithmetic operations (price - 500 > 200)
 */
public class ExpressionCondition implements Condition {
    
    private final String expression;
    
    public ExpressionCondition(String expression) {
        this.expression = expression;
    }
    
    @Override
    public boolean evaluate(RuleContext context) {
        try {
            // Simple implementation for demonstration
            // In a real-world scenario, you might use a library like OGNL or SpEL
            // For this example, we'll implement basic support for common cases
            
            // Parse and evaluate the expression
            boolean result = evaluateExpression(expression, context);
            System.out.println("ExpressionCondition: Evaluated expression '" + expression + "' to " + result);
            return result;
        } catch (Exception e) {
            System.out.println("ExpressionCondition: Error evaluating expression '" + expression + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean evaluateExpression(String expr, RuleContext context) throws Exception {
        // Trim whitespace
        expr = expr.trim();
        System.out.println("ExpressionCondition: Evaluating expression '" + expr + "'");
        
        // Handle logical AND (&&) first - left to right evaluation
        if (expr.contains(" && ")) {
            String[] parts = expr.split(" && ");
            System.out.println("ExpressionCondition: Splitting AND expression into parts: " + Arrays.toString(parts));
            for (String part : parts) {
                boolean partResult = evaluateExpression(part.trim(), context);
                System.out.println("ExpressionCondition: AND part '" + part + "' evaluated to " + partResult);
                if (!partResult) {
                    return false;
                }
            }
            return true;
        }
        
        // Handle logical OR (||) - left to right evaluation
        if (expr.contains(" || ")) {
            String[] parts = expr.split(" || ");
            System.out.println("ExpressionCondition: Splitting OR expression into parts: " + Arrays.toString(parts));
            for (String part : parts) {
                boolean partResult = evaluateExpression(part.trim(), context);
                System.out.println("ExpressionCondition: OR part '" + part + "' evaluated to " + partResult);
                if (partResult) {
                    return true;
                }
            }
            return false;
        }
        
        // Handle simple comparisons
        if (expr.contains(" > ")) {
            String[] parts = expr.split(" > ");
            System.out.println("ExpressionCondition: Splitting > expression into parts: " + Arrays.toString(parts));
            Object left = evaluateValue(parts[0].trim(), context);
            Object right = evaluateValue(parts[1].trim(), context);
            System.out.println("ExpressionCondition: Left value: " + left + ", Right value: " + right);
            return compare(left, right) > 0;
        } else if (expr.contains(" < ")) {
            String[] parts = expr.split(" < ");
            System.out.println("ExpressionCondition: Splitting < expression into parts: " + Arrays.toString(parts));
            Object left = evaluateValue(parts[0].trim(), context);
            Object right = evaluateValue(parts[1].trim(), context);
            System.out.println("ExpressionCondition: Left value: " + left + ", Right value: " + right);
            return compare(left, right) < 0;
        } else if (expr.contains(" == ")) {
            String[] parts = expr.split(" == ");
            System.out.println("ExpressionCondition: Splitting == expression into parts: " + Arrays.toString(parts));
            Object left = evaluateValue(parts[0].trim(), context);
            Object right = evaluateValue(parts[1].trim(), context);
            System.out.println("ExpressionCondition: Left value: " + left + ", Right value: " + right);
            return compare(left, right) == 0;
        } else if (expr.contains(" != ")) {
            String[] parts = expr.split(" != ");
            System.out.println("ExpressionCondition: Splitting != expression into parts: " + Arrays.toString(parts));
            Object left = evaluateValue(parts[0].trim(), context);
            Object right = evaluateValue(parts[1].trim(), context);
            System.out.println("ExpressionCondition: Left value: " + left + ", Right value: " + right);
            return compare(left, right) != 0;
        } else if (expr.contains(" >= ")) {
            String[] parts = expr.split(" >= ");
            System.out.println("ExpressionCondition: Splitting >= expression into parts: " + Arrays.toString(parts));
            Object left = evaluateValue(parts[0].trim(), context);
            Object right = evaluateValue(parts[1].trim(), context);
            System.out.println("ExpressionCondition: Left value: " + left + ", Right value: " + right);
            return compare(left, right) >= 0;
        } else if (expr.contains(" <= ")) {
            String[] parts = expr.split(" <= ");
            System.out.println("ExpressionCondition: Splitting <= expression into parts: " + Arrays.toString(parts));
            Object left = evaluateValue(parts[0].trim(), context);
            Object right = evaluateValue(parts[1].trim(), context);
            System.out.println("ExpressionCondition: Left value: " + left + ", Right value: " + right);
            return compare(left, right) <= 0;
        }
        
        // Handle boolean expressions
        boolean result = Boolean.parseBoolean(expr);
        System.out.println("ExpressionCondition: Boolean expression evaluated to " + result);
        return result;
    }
    
    private Object evaluateValue(String valueExpr, RuleContext context) throws Exception {
        // Trim whitespace
        valueExpr = valueExpr.trim();
        System.out.println("ExpressionCondition: Evaluating value expression '" + valueExpr + "'");
        
        // Check if it's a number
        if (valueExpr.matches("\\d+(\\.\\d+)?")) {
            Object result;
            if (valueExpr.contains(".")) {
                result = Double.parseDouble(valueExpr);
            } else {
                result = Integer.parseInt(valueExpr);
            }
            System.out.println("ExpressionCondition: Numeric literal evaluated to " + result);
            return result;
        }
        
        // Check if it's a string literal
        if (valueExpr.startsWith("'") && valueExpr.endsWith("'")) {
            String result = valueExpr.substring(1, valueExpr.length() - 1);
            System.out.println("ExpressionCondition: String literal evaluated to '" + result + "'");
            return result;
        }
        
        // Check if it's a boolean literal
        if (valueExpr.equals("true") || valueExpr.equals("false")) {
            Boolean result = Boolean.parseBoolean(valueExpr);
            System.out.println("ExpressionCondition: Boolean literal evaluated to " + result);
            return result;
        }
        
        // Check if it's a property access
        if (valueExpr.contains(".")) {
            Object result = evaluatePropertyAccess(valueExpr, context);
            System.out.println("ExpressionCondition: Property access '" + valueExpr + "' evaluated to " + result);
            return result;
        }
        
        // Check if it's a method call
        if (valueExpr.contains("()")) {
            Object result = evaluateMethodCall(valueExpr, context);
            System.out.println("ExpressionCondition: Method call '" + valueExpr + "' evaluated to " + result);
            return result;
        }
        
        // Check if it's an array access
        if (valueExpr.contains("[") && valueExpr.contains("]")) {
            Object result = evaluateArrayAccess(valueExpr, context);
            System.out.println("ExpressionCondition: Array access '" + valueExpr + "' evaluated to " + result);
            return result;
        }
        
        // Check if it's an arithmetic expression
        if (valueExpr.contains("+") || valueExpr.contains("-") || valueExpr.contains("*") || valueExpr.contains("/")) {
            Object result = evaluateArithmetic(valueExpr, context);
            System.out.println("ExpressionCondition: Arithmetic expression '" + valueExpr + "' evaluated to " + result);
            return result;
        }
        
        // Otherwise, try to get from context
        Object result = context.get(valueExpr);
        System.out.println("ExpressionCondition: Context variable '" + valueExpr + "' evaluated to " + result);
        return result;
    }
    
    private Object evaluatePropertyAccess(String propertyPath, Object context) throws Exception {
        System.out.println("ExpressionCondition: Evaluating property access '" + propertyPath + "'");
        
        // First, check if the propertyPath contains array access
        if (propertyPath.contains("[") && propertyPath.contains("]")) {
            // Handle array access first
            int start = propertyPath.indexOf("[");
            int end = propertyPath.indexOf("]");
            String basePath = propertyPath.substring(0, start);
            String arrayExpr = propertyPath.substring(start, end + 1);
            System.out.println("ExpressionCondition: Array access detected - basePath: '" + basePath + "', arrayExpr: '" + arrayExpr + "'");
            
            // Evaluate the base path
            Object base = evaluatePropertyAccess(basePath, context);
            System.out.println("ExpressionCondition: Base path evaluated to: " + base);
            
            // Evaluate the array access
            Object result = evaluateArrayAccess(arrayExpr, base);
            System.out.println("ExpressionCondition: Array access result: " + result);
            return result;
        }
        
        // Check if it's a method call
        if (propertyPath.endsWith("()")) {
            // Evaluate the property without the ()
            String propertyName = propertyPath.substring(0, propertyPath.length() - 2);
            System.out.println("ExpressionCondition: Method call detected - propertyName: '" + propertyName + "'");
            Object obj = evaluatePropertyAccess(propertyName, context);
            System.out.println("ExpressionCondition: Target object for method call: " + obj);
            
            // Call the method
            Object result = evaluateMethodCall(propertyPath, obj);
            System.out.println("ExpressionCondition: Method call result: " + result);
            return result;
        }
        
        // Check if it's a nested property access
        if (propertyPath.contains(".")) {
            String[] parts = propertyPath.split("\\.");
            Object current = null;
            if (context instanceof RuleContext) {
                current = ((RuleContext) context).get(parts[0]);
            } else {
                current = getProperty(context, parts[0]);
            }
            System.out.println("ExpressionCondition: Root property '" + parts[0] + "' from context: " + current);
            
            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                System.out.println("ExpressionCondition: Processing part '" + part + "' on current object: " + current);
                current = evaluatePropertyAccess(part, current);
                System.out.println("ExpressionCondition: After processing part '" + part + "', current object: " + current);
            }
            
            System.out.println("ExpressionCondition: Regular property access result: " + current);
            return current;
        }
        
        // Simple property access
        Object result;
        if (context instanceof RuleContext) {
            result = ((RuleContext) context).get(propertyPath);
        } else {
            result = getProperty(context, propertyPath);
        }
        System.out.println("ExpressionCondition: Simple property access result: " + result);
        return result;
    }
    
    private Object evaluateArrayAccess(String arrayExpr, Object context) throws Exception {
        // Extract array name and index
        int start = arrayExpr.indexOf("[");
        int end = arrayExpr.indexOf("]");
        String arrayName = arrayExpr.substring(0, start);
        int index = Integer.parseInt(arrayExpr.substring(start + 1, end));
        
        Object array = evaluatePropertyAccess(arrayName, context);
        
        if (array instanceof List) {
            return ((List<?>) array).get(index);
        } else if (array instanceof Object[]) {
            return ((Object[]) array)[index];
        }
        
        throw new IllegalArgumentException("Not an array or list: " + arrayName);
    }
    
    private Object evaluateMethodCall(String methodExpr, Object context) throws Exception {
        String methodName = methodExpr.substring(0, methodExpr.length() - 2);
        
        // Handle special case: size() for collections
        if (methodName.equals("size") && context instanceof List) {
            return ((List<?>) context).size();
        } else if (methodName.equals("size") && context instanceof Map) {
            return ((Map<?, ?>) context).size();
        } else if (methodName.equals("isEmpty") && context instanceof List) {
            return ((List<?>) context).isEmpty();
        } else if (methodName.equals("isEmpty") && context instanceof Map) {
            return ((Map<?, ?>) context).isEmpty();
        }
        
        // Try to invoke method via reflection
        Method method = context.getClass().getMethod(methodName);
        return method.invoke(context);
    }
    
    private Object evaluateArithmetic(String expr, RuleContext context) throws Exception {
        // Simple arithmetic evaluation for demonstration
        // This is a basic implementation, in real life use a proper parser
        
        // Handle subtraction first (since it can be unary)
        if (expr.contains(" - ")) {
            String[] parts = expr.split(" - ");
            Object left = evaluateValue(parts[0].trim(), context);
            Object right = evaluateValue(parts[1].trim(), context);
            return ((Number) left).doubleValue() - ((Number) right).doubleValue();
        } else if (expr.contains(" + ")) {
            String[] parts = expr.split(" + ");
            Object left = evaluateValue(parts[0].trim(), context);
            Object right = evaluateValue(parts[1].trim(), context);
            return ((Number) left).doubleValue() + ((Number) right).doubleValue();
        } else if (expr.contains(" * ")) {
            String[] parts = expr.split(" * ");
            Object left = evaluateValue(parts[0].trim(), context);
            Object right = evaluateValue(parts[1].trim(), context);
            return ((Number) left).doubleValue() * ((Number) right).doubleValue();
        } else if (expr.contains(" / ")) {
            String[] parts = expr.split(" / ");
            Object left = evaluateValue(parts[0].trim(), context);
            Object right = evaluateValue(parts[1].trim(), context);
            return ((Number) left).doubleValue() / ((Number) right).doubleValue();
        }
        
        return null;
    }
    
    private Object getProperty(Object obj, String propertyName) throws Exception {
        // Try getter method first
        String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        try {
            Method method = obj.getClass().getMethod(getterName);
            return method.invoke(obj);
        } catch (NoSuchMethodException e) {
            // Try is method for boolean properties
            getterName = "is" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            try {
                Method method = obj.getClass().getMethod(getterName);
                return method.invoke(obj);
            } catch (NoSuchMethodException ex) {
                // Try direct field access
                java.lang.reflect.Field field = obj.getClass().getDeclaredField(propertyName);
                field.setAccessible(true);
                return field.get(obj);
            }
        }
    }
    
    private int compare(Object left, Object right) {
        if (left == null && right == null) return 0;
        if (left == null) return -1;
        if (right == null) return 1;
        
        if (left instanceof Number && right instanceof Number) {
            double leftVal = ((Number) left).doubleValue();
            double rightVal = ((Number) right).doubleValue();
            return Double.compare(leftVal, rightVal);
        }
        
        if (left instanceof String && right instanceof String) {
            return ((String) left).compareTo((String) right);
        }
        
        if (left instanceof Boolean && right instanceof Boolean) {
            return ((Boolean) left).compareTo((Boolean) right);
        }
        
        return left.toString().compareTo(right.toString());
    }
}