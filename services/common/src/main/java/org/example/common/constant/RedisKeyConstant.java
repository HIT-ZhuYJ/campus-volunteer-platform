package org.example.common.constant;

public class RedisKeyConstant {
    
    public static final String ACTIVITY_STOCK_PREFIX = "activity:stock:";
    
    public static final String ACTIVITY_LOCK_PREFIX = "activity:lock:";
    
    public static final String USER_REGISTER_PREFIX = "user:register:";
    
    public static String getActivityStockKey(Long activityId) {
        return ACTIVITY_STOCK_PREFIX + activityId;
    }
    
    public static String getActivityLockKey(Long activityId, Long userId) {
        return ACTIVITY_LOCK_PREFIX + activityId + ":" + userId;
    }
}
