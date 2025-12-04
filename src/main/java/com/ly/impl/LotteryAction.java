package com.ly.impl;

import com.ly.core.Action;
import com.ly.core.RuleContext;
import com.ly.service.LotteryService;

import java.util.Date;
import java.util.List;

/**
 * 抽奖动作类
 * 实现Action接口，将抽奖功能作为规则的一个动作来执行
 */
public class LotteryAction implements Action {
    
    private final String usersKey; // RuleContext中用户列表的键名
    private final Date startTime; // 开始时间
    private final Date endTime; // 结束时间
    private final int numberOfWinners; // 中奖者数量
    private final String winnersKey; // RuleContext中存放中奖者列表的键名
    private final LotteryService lotteryService; // 抽奖服务
    
    /**
     * 构造函数
     * @param usersKey RuleContext中用户列表的键名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param numberOfWinners 中奖者数量
     * @param winnersKey RuleContext中存放中奖者列表的键名
     */
    public LotteryAction(String usersKey, Date startTime, Date endTime, int numberOfWinners, String winnersKey) {
        this.usersKey = usersKey;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberOfWinners = numberOfWinners;
        this.winnersKey = winnersKey;
        this.lotteryService = new LotteryService();
    }
    
    /**
     * 构造函数
     * @param usersKey RuleContext中用户列表的键名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param numberOfWinners 中奖者数量
     * @param winnersKey RuleContext中存放中奖者列表的键名
     * @param lotteryService 抽奖服务
     */
    public LotteryAction(String usersKey, Date startTime, Date endTime, int numberOfWinners, String winnersKey, LotteryService lotteryService) {
        this.usersKey = usersKey;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberOfWinners = numberOfWinners;
        this.winnersKey = winnersKey;
        this.lotteryService = lotteryService;
    }
    
    @Override
    public void execute(RuleContext context) {
        try {
            // 从RuleContext中获取用户列表
            List<?> users = (List<?>) context.get(usersKey);
            
            // 进行抽奖
            List<?> winners = lotteryService.drawLottery(users, startTime, endTime, numberOfWinners);
            
            // 将中奖者列表放入RuleContext中
            context.put(winnersKey, winners);
            
            System.out.println("抽奖完成，共抽取了" + winners.size() + "名中奖者");
        } catch (Exception e) {
            throw new RuntimeException("抽奖动作执行失败", e);
        }
    }
    
    // Getters
    public String getUsersKey() { return usersKey; }
    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }
    public int getNumberOfWinners() { return numberOfWinners; }
    public String getWinnersKey() { return winnersKey; }
}
