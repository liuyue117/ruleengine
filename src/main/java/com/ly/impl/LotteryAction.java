package com.ly.impl;

import com.ly.core.Action;
import com.ly.core.RuleContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * An action that performs random lottery drawing for customers who made purchases in a specific time period.
 * 抽奖操作，为在特定时间段内购买的客户进行随机抽奖
 */
public class LotteryAction implements Action {
    private static final Random random = new Random();
    
    private final String name;
    private final int winnerCount; // Number of winners to select
    private final ChronoUnit timeUnit; // Time unit for the period (minutes or hours)
    private final long periodValue; // Value of the time period
    private final String customerListKey; // Key in context for customer list
    private final String resultKey; // Key in context to store winners
    
    /**
     * Creates a new LotteryAction.
     * @param name Action name
     * @param winnerCount Number of winners to select
     * @param timeUnit Time unit for the period
     * @param periodValue Value of the time period
     * @param customerListKey Key in context for customer list
     * @param resultKey Key in context to store winners
     */
    public LotteryAction(String name, int winnerCount, ChronoUnit timeUnit, long periodValue, 
                        String customerListKey, String resultKey) {
        this.name = name;
        this.winnerCount = winnerCount;
        this.timeUnit = timeUnit;
        this.periodValue = periodValue;
        this.customerListKey = customerListKey;
        this.resultKey = resultKey;
    }
    
    @Override
    public void execute(RuleContext context) {
        System.out.println("Executing lottery action: " + name);
        
        // Get customer list from context
        List<CustomerPurchase> customers = context.get(customerListKey, List.class);
        if (customers == null || customers.isEmpty()) {
            System.out.println("No customers to draw lottery from");
            context.put(resultKey, new ArrayList<>());
            return;
        }
        
        // Calculate time range
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minus(periodValue, timeUnit);
        
        // Filter customers who made purchases in the specified time period
        List<CustomerPurchase> eligibleCustomers = customers.stream()
            .filter(customer -> customer.getPurchaseTime().isAfter(startTime))
            .filter(customer -> customer.getPurchaseTime().isBefore(now))
            .collect(Collectors.toList());
        
        if (eligibleCustomers.isEmpty()) {
            System.out.println("No eligible customers for this lottery period");
            context.put(resultKey, new ArrayList<>());
            return;
        }
        
        // Select random winners
        List<CustomerPurchase> winners = selectRandomWinners(eligibleCustomers, winnerCount);
        
        // Store winners in context
        context.put(resultKey, winners);
        
        // Print results
        System.out.println("Lottery drawing completed!");
        System.out.println("Time period: " + periodValue + " " + timeUnit + "(s) ending at " + now);
        System.out.println("Eligible customers: " + eligibleCustomers.size());
        System.out.println("Winners selected: " + winners.size());
        
        for (int i = 0; i < winners.size(); i++) {
            CustomerPurchase winner = winners.get(i);
            System.out.println((i + 1) + ". Customer ID: " + winner.getCustomerId() + ", Purchase Time: " + winner.getPurchaseTime());
        }
    }
    
    /**
     * Selects random winners from the eligible customers.
     * @param eligibleCustomers List of eligible customers
     * @param winnerCount Number of winners to select
     * @return List of selected winners
     */
    private List<CustomerPurchase> selectRandomWinners(List<CustomerPurchase> eligibleCustomers, int winnerCount) {
        List<CustomerPurchase> shuffled = new ArrayList<>(eligibleCustomers);
        Collections.shuffle(shuffled, random);
        
        int actualWinnerCount = Math.min(winnerCount, shuffled.size());
        return shuffled.subList(0, actualWinnerCount);
    }
    
    /**
     * Represents a customer's purchase record.
     * 表示客户的购买记录
     */
    public static class CustomerPurchase {
        private final String customerId;
        private final LocalDateTime purchaseTime;
        
        public CustomerPurchase(String customerId, LocalDateTime purchaseTime) {
            this.customerId = customerId;
            this.purchaseTime = purchaseTime;
        }
        
        public String getCustomerId() {
            return customerId;
        }
        
        public LocalDateTime getPurchaseTime() {
            return purchaseTime;
        }
        
        @Override
        public String toString() {
            return "CustomerPurchase{" +
                    "customerId='" + customerId + '\'' +
                    ", purchaseTime=" + purchaseTime +
                    '}';
        }
    }
}