package io.github.quiethappiness.wrench.traffic.control.domain.model.valobj;


/**
 * TrafficControlContext
 * @author quietHappiness
 * @description 白名单上下文，用于在AOP之间传递状态
 */
public class TrafficControlContext
{
    
    private static final ThreadLocal<Boolean> inWhiteList = new ThreadLocal<>();
    
    public static void setInWhiteList(boolean value) {
        inWhiteList.set(value);
    }
    
    public static boolean isInWhiteList() {
        Boolean value = inWhiteList.get();
        return value != null && value;
    }
    
    public static void clear() {
        inWhiteList.remove();
    }
}

