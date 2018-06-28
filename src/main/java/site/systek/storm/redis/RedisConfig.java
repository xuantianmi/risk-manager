package site.systek.storm.redis;

public class RedisConfig {
    
    public static final String REDIS_HOST = "60.205.165.163";
    
    public static final Integer REDIS_PORT = 6379;
    
    public static final Integer REDIS_BASE = 12;
    
    public static final String REDIS_AUTH = "S20e6Ys53k";
    
    /**
     * 风险控制服务从此KEY中读取每个奖期的销售数据
     */
    public final static String REDIS_SPOUT_KEY_NAME = "hrb.storm:stake";
    
    /**
     * 风险控制服务往此KEY中写入每个奖期的统计数据
     */
    public final static String REDIS_BOLT_KEY_NAME = "hrb.storm:volume";
}
