package com.ly.expression;

import com.ly.core.RuleContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

/**
 * 简单表达式求值器实现
 * 使用Java的ScriptEngineManager来实现表达式求值
 */
public class SimpleExpressionEvaluator implements ExpressionEvaluator {
    
    private static final ScriptEngineManager ENGINE_MANAGER = new ScriptEngineManager();
    private static final ScriptEngine JS_ENGINE = ENGINE_MANAGER.getEngineByName("JavaScript");
    
    @Override
    public Object evaluate(String expression, RuleContext context) {
        try {
            // 将RuleContext中的数据添加到脚本引擎的全局范围
            Map<String, Object> data = context.getData();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                JS_ENGINE.put(entry.getKey(), entry.getValue());
            }
            
            // 计算表达式并返回结果
            return JS_ENGINE.eval(expression);
        } catch (ScriptException e) {
            // 尝试处理嵌套层级的表达式，例如 user.goods.size()
            // 这需要我们手动解析表达式中的层级结构
            // 首先，我们需要检查表达式是否包含点号(.)
            if (expression.contains(".")) {
                try {
                    return evaluateNestedExpression(expression, context);
                } catch (Exception ex) {
                    // 如果手动处理也失败了，就抛出原始异常
                    throw new RuntimeException("表达式求值失败: " + expression, e);
                }
            } else {
                // 如果表达式不包含点号，就直接抛出原始异常
                throw new RuntimeException("表达式求值失败: " + expression, e);
            }
        } finally {
            // 清理脚本引擎的全局范围，避免数据污染
            Map<String, Object> data = context.getData();
            for (String key : data.keySet()) {
                JS_ENGINE.put(key, null);
            }
        }
    }
    
    /**
     * 手动解析和计算嵌套层级的表达式
     * 例如：user.goods.size() > 3
     * @param expression 要解析的表达式
     * @param context 规则上下文
     * @return 表达式计算结果
     */
    private Object evaluateNestedExpression(String expression, RuleContext context) throws Exception {
        // 这里我们需要手动解析表达式中的层级结构
        // 首先，我们需要找到表达式中的第一个操作符(>、<、==、!=、&&、||等)
        // 然后，我们需要将表达式分成两部分：左边的对象路径和右边的比较值
        // 例如，对于表达式 "user.goods.size() > 3"，我们需要将其分成：
        // - 左边的对象路径："user.goods.size()"
        // - 右边的比较值："3"
        // - 操作符：">"
        
        // 首先，我们需要找到表达式中的第一个操作符
        int operatorIndex = findFirstOperatorIndex(expression);
        if (operatorIndex == -1) {
            // 如果表达式中没有操作符，就直接返回对象路径的值
            return getValueByPath(expression, context);
        }
        
        // 然后，我们需要将表达式分成两部分：左边的对象路径和右边的比较值
        String leftPath = expression.substring(0, operatorIndex).trim();
        String operator = expression.substring(operatorIndex, operatorIndex + 1).trim();
        // 检查是否有两个字符的操作符，例如 >=、<=、==、!=、&&、||等
        if (operatorIndex + 1 < expression.length()) {
            String nextChar = expression.substring(operatorIndex + 1, operatorIndex + 2);
            if (nextChar.equals(">") || nextChar.equals("<") || nextChar.equals("=") || nextChar.equals("!") || nextChar.equals("&") || nextChar.equals("|")) {
                operator += nextChar;
            }
        }
        String rightValue = expression.substring(operatorIndex + operator.length()).trim();
        
        // 然后，我们需要获取左边对象路径的值
        Object leftObj = getValueByPath(leftPath, context);
        
        // 然后，我们需要将右边的比较值转换为适当的类型
        Object rightObj = convertToType(rightValue, leftObj.getClass());
        
        // 最后，我们需要根据操作符进行比较，并返回结果
        return compareValues(leftObj, rightObj, operator);
    }
    
    /**
     * 找到表达式中的第一个操作符的索引
     * 操作符包括：>、<、==、!=、&&、||等
     * @param expression 要查找的表达式
     * @return 第一个操作符的索引，如果没有找到操作符，就返回-1
     */
    private int findFirstOperatorIndex(String expression) {
        int index = -1;
        // 先查找两个字符的操作符
        String[] doubleOperators = {">=", "<=", "==", "!=", "&&", "||"};
        for (String op : doubleOperators) {
            int opIndex = expression.indexOf(op);
            if (opIndex != -1 && (index == -1 || opIndex < index)) {
                index = opIndex;
            }
        }
        
        // 如果没有找到两个字符的操作符，就查找单个字符的操作符
        if (index == -1) {
            String[] singleOperators = {">", "<", "=", "!", "&", "|"};
            for (String op : singleOperators) {
                int opIndex = expression.indexOf(op);
                if (opIndex != -1 && (index == -1 || opIndex < index)) {
                    index = opIndex;
                }
            }
        }
        
        return index;
    }
    
    /**
     * 根据对象路径获取值
     * 例如：对于路径 "user.goods.size()"，我们需要先从规则上下文中获取 "user" 对象
     * @param path 对象路径
     * @param context 规则上下文
     * @return 对象路径的值
     */
    private Object getValueByPath(String path, RuleContext context) throws Exception {
        // 找到路径中的第一个点号(.)
        int dotIndex = path.indexOf(".");
        if (dotIndex == -1) {
            // 如果路径中没有点号，就直接从规则上下文中获取值
            return context.getData().get(path);
        }
        
        // 从规则上下文中获取顶层对象
        String topLevelKey = path.substring(0, dotIndex).trim();
        Object topLevelObj = context.getData().get(topLevelKey);
        if (topLevelObj == null) {
            throw new RuntimeException("对象路径不存在: " + path);
        }
        
        // 然后，我们需要根据剩余的路径获取嵌套对象的值
        String remainingPath = path.substring(dotIndex + 1).trim();
        return getNestedValue(topLevelObj, remainingPath);
    }
    
    /**
     * 根据剩余的路径获取嵌套对象的值
     * 例如：对于顶层对象 "user" 和剩余路径 "goods.size()"，我们需要获取 "user.goods.size()" 的值
     * @param obj 顶层对象
     * @param remainingPath 剩余的路径
     * @return 嵌套对象的值
     */
    private Object getNestedValue(Object obj, String remainingPath) throws Exception {
        // 检查剩余路径是否包含点号(.)
        int dotIndex = remainingPath.indexOf(".");
        if (dotIndex == -1) {
            // 如果剩余路径中没有点号，就直接调用对象的方法或获取对象的属性
            return getPropertyOrCallMethod(obj, remainingPath);
        }
        
        // 如果剩余路径中包含点号，就先获取当前层级的对象，然后再递归调用 getNestedValue 方法
        String currentPath = remainingPath.substring(0, dotIndex).trim();
        Object currentObj = getPropertyOrCallMethod(obj, currentPath);
        if (currentObj == null) {
            throw new RuntimeException("对象路径不存在: " + remainingPath);
        }
        
        String newRemainingPath = remainingPath.substring(dotIndex + 1).trim();
        return getNestedValue(currentObj, newRemainingPath);
    }
    
    /**
     * 获取对象的属性或调用对象的方法
     * 例如：对于对象 "goods" 和路径 "size()"，我们需要调用 "goods.size()" 方法
     * 对于对象 "user" 和路径 "name"，我们需要获取 "user.name" 属性
     * 对于对象 "goods" 和路径 "get(0)"，我们需要调用 "goods.get(0)" 方法
     * @param obj 对象
     * @param path 属性或方法的路径
     * @return 属性或方法的返回值
     */
    private Object getPropertyOrCallMethod(Object obj, String path) throws Exception {
        // 检查路径是否包含括号(()
        if (path.contains("()")) {
            // 如果路径包含括号，就调用对象的方法
            String methodName = path.substring(0, path.indexOf("()")).trim();
            // 调用对象的方法，这里假设方法没有参数
            java.lang.reflect.Method method = obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } else if (path.contains("get(")) {
            // 如果路径包含 "get("，就调用对象的 get 方法
            // 例如：对于路径 "get(0)"，我们需要调用 "get(0)" 方法
            int startIndex = path.indexOf("get(") + 4; // 4 是 "get(" 的长度
            int endIndex = path.indexOf(")", startIndex);
            if (endIndex == -1) {
                throw new RuntimeException("无效的 get 方法调用: " + path);
            }
            
            // 获取 get 方法的参数
            String paramStr = path.substring(startIndex, endIndex).trim();
            int param = Integer.parseInt(paramStr);
            
            // 调用对象的 get 方法
            java.lang.reflect.Method method = obj.getClass().getMethod("get", int.class);
            return method.invoke(obj, param);
        } else {
            // 如果路径不包含括号，就获取对象的属性
            String propertyName = path.trim();
            // 获取对象的属性，这里使用Java的反射机制
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            return field.get(obj);
        }
    }
    
    /**
     * 将字符串转换为适当的类型
     * @param value 要转换的字符串
     * @param targetType 目标类型
     * @return 转换后的对象
     */
    private Object convertToType(String value, Class<?> targetType) throws Exception {
        // 这里我们需要根据目标类型将字符串转换为适当的类型
        // 例如，如果目标类型是整数，我们需要将字符串转换为整数
        // 如果目标类型是浮点数，我们需要将字符串转换为浮点数
        // 如果目标类型是布尔值，我们需要将字符串转换为布尔值
        
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value);
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.parseFloat(value);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == String.class) {
            // 如果目标类型是字符串，我们需要去除字符串两边的引号
            if ((value.startsWith("'") && value.endsWith("'") || (value.startsWith("\"") && value.endsWith("\"")))) {
                return value.substring(1, value.length() - 1);
            } else {
                return value;
            }
        } else {
            // 如果目标类型不是上述类型，我们就直接返回字符串
            return value;
        }
    }
    
    /**
     * 根据操作符比较两个值
     * @param leftObj 左边的值
     * @param rightObj 右边的值
     * @param operator 操作符
     * @return 比较结果
     */
    private Object compareValues(Object leftObj, Object rightObj, String operator) throws Exception {
        // 这里我们需要根据操作符比较两个值
        // 例如，如果操作符是 ">"，我们需要比较左边的值是否大于右边的值
        
        if (operator.equals(">")) {
            if (leftObj instanceof Integer && rightObj instanceof Integer) {
                return (Integer) leftObj > (Integer) rightObj;
            } else if (leftObj instanceof Long && rightObj instanceof Long) {
                return (Long) leftObj > (Long) rightObj;
            } else if (leftObj instanceof Float && rightObj instanceof Float) {
                return (Float) leftObj > (Float) rightObj;
            } else if (leftObj instanceof Double && rightObj instanceof Double) {
                return (Double) leftObj > (Double) rightObj;
            } else {
                throw new RuntimeException("不支持的比较类型: " + leftObj.getClass() + " 和 " + rightObj.getClass());
            }
        } else if (operator.equals("<")) {
            if (leftObj instanceof Integer && rightObj instanceof Integer) {
                return (Integer) leftObj < (Integer) rightObj;
            } else if (leftObj instanceof Long && rightObj instanceof Long) {
                return (Long) leftObj < (Long) rightObj;
            } else if (leftObj instanceof Float && rightObj instanceof Float) {
                return (Float) leftObj < (Float) rightObj;
            } else if (leftObj instanceof Double && rightObj instanceof Double) {
                return (Double) leftObj < (Double) rightObj;
            } else {
                throw new RuntimeException("不支持的比较类型: " + leftObj.getClass() + " 和 " + rightObj.getClass());
            }
        } else if (operator.equals(">=")) {
            if (leftObj instanceof Integer && rightObj instanceof Integer) {
                return (Integer) leftObj >= (Integer) rightObj;
            } else if (leftObj instanceof Long && rightObj instanceof Long) {
                return (Long) leftObj >= (Long) rightObj;
            } else if (leftObj instanceof Float && rightObj instanceof Float) {
                return (Float) leftObj >= (Float) rightObj;
            } else if (leftObj instanceof Double && rightObj instanceof Double) {
                return (Double) leftObj >= (Double) rightObj;
            } else {
                throw new RuntimeException("不支持的比较类型: " + leftObj.getClass() + " 和 " + rightObj.getClass());
            }
        } else if (operator.equals("<=")) {
            if (leftObj instanceof Integer && rightObj instanceof Integer) {
                return (Integer) leftObj <= (Integer) rightObj;
            } else if (leftObj instanceof Long && rightObj instanceof Long) {
                return (Long) leftObj <= (Long) rightObj;
            } else if (leftObj instanceof Float && rightObj instanceof Float) {
                return (Float) leftObj <= (Float) rightObj;
            } else if (leftObj instanceof Double && rightObj instanceof Double) {
                return (Double) leftObj <= (Double) rightObj;
            } else {
                throw new RuntimeException("不支持的比较类型: " + leftObj.getClass() + " 和 " + rightObj.getClass());
            }
        } else if (operator.equals("==")) {
            return leftObj.equals(rightObj);
        } else if (operator.equals("!=")) {
            return !leftObj.equals(rightObj);
        } else if (operator.equals("&&")) {
            if (leftObj instanceof Boolean && rightObj instanceof Boolean) {
                return (Boolean) leftObj && (Boolean) rightObj;
            } else {
                throw new RuntimeException("不支持的比较类型: " + leftObj.getClass() + " 和 " + rightObj.getClass());
            }
        } else if (operator.equals("||")) {
            if (leftObj instanceof Boolean && rightObj instanceof Boolean) {
                return (Boolean) leftObj || (Boolean) rightObj;
            } else {
                throw new RuntimeException("不支持的比较类型: " + leftObj.getClass() + " 和 " + rightObj.getClass());
            }
        } else {
            throw new RuntimeException("不支持的操作符: " + operator);
        }
    }
}