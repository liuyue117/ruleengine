package com.ly.model;

import java.util.Date;

/**
 * 购买者接口
 * 定义购买者类必须实现的方法
 */
public interface Purchaser {
    
    /**
     * 获取购买时间
     * @return 购买时间
     */
    Date getPurchaseTime();
}
