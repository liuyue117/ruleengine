package com.ly.service;

import com.ly.model.Purchaser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 抽奖服务类
 * 支持按时间段抽取指定数量的中奖者
 */
public class LotteryService {
    
    /**
     * 从指定时间段内的用户列表中随机抽取指定数量的中奖者
     * @param users 用户列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param numberOfWinners 中奖者数量
     * @return 中奖者列表
     */
    public <T> List<T> drawLottery(List<T> users, Date startTime, Date endTime, int numberOfWinners) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        
        if (numberOfWinners <= 0) {
            return Collections.emptyList();
        }
        
        // 过滤出在指定时间段内的用户
        List<T> eligibleUsers = filterUsersByTimeRange(users, startTime, endTime);
        
        if (eligibleUsers.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 随机排序用户列表
        Collections.shuffle(eligibleUsers);
        
        // 选择前numberOfWinners个用户作为中奖者
        int actualNumberOfWinners = Math.min(numberOfWinners, eligibleUsers.size());
        return eligibleUsers.subList(0, actualNumberOfWinners);
    }
    
    /**
     * 过滤出在指定时间段内的用户
     * 这里假设用户类实现了Purchaser接口
     * @param users 用户列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 符合条件的用户列表
     */
    private <T> List<T> filterUsersByTimeRange(List<T> users, Date startTime, Date endTime) {
        List<T> filteredUsers = new ArrayList<>();
        
        for (T user : users) {
            try {
                // 检查用户是否实现了Purchaser接口
                if (user instanceof Purchaser) {
                    Purchaser purchaser = (Purchaser) user;
                    Date purchaseTime = purchaser.getPurchaseTime();
                    
                    // 检查购买时间是否在指定时间段内
                    if (purchaseTime != null && 
                        (startTime == null || purchaseTime.after(startTime) || purchaseTime.equals(startTime)) && 
                        (endTime == null || purchaseTime.before(endTime) || purchaseTime.equals(endTime))) {
                        filteredUsers.add(user);
                    }
                }
            } catch (Exception e) {
                // 如果类型转换失败，跳过这个用户
                e.printStackTrace();
            }
        }
        
        return filteredUsers;
    }
}
