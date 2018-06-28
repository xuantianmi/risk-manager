package site.systek.storm.topology;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.redis.bolt.RedisStoreBolt;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.common.mapper.RedisDataTypeDescription;
import org.apache.storm.redis.common.mapper.RedisStoreMapper;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.ITuple;

import site.systek.storm.bolt.RiskMng1HorseBolt;
import site.systek.storm.redis.RedisConfig;
import site.systek.storm.spout.RiskMng4HorseSpout;

/**
 * Risk Management for High frequecy fixed horse.
 * 
 * @author Merlin
 *
 */
public class RiskMng1Topology {
    
    public static String REDIS_HOST = RedisConfig.REDIS_HOST;
    
    public static Integer REDIS_PORT = RedisConfig.REDIS_PORT;
    
    public static Integer REDIS_BASE = RedisConfig.REDIS_BASE;
    
    public static String REDIS_AUTH = RedisConfig.REDIS_AUTH;
    
    /**
     * storm jar risk-manager-0.0.1.jar
     * site.systek.storm.topology.RiskMng1Topology <redis host> <redis port> <redis password> <redis database> (topology name)
     * 
     * @param args : if args is null or length < 4 ,use the default config
     * @throws InterruptedException
     * @throws AuthorizationException
     * @throws InvalidTopologyException
     * @throws AlreadyAliveException
     */
    public static void main(String[] args) throws Exception {
        if (args.length >= 4) {
            REDIS_HOST = args[0];
            REDIS_PORT = Integer.parseInt(args[1]);
            REDIS_AUTH = args[2];
            REDIS_BASE = Integer.parseInt(args[3]);
        }
        Config conf = new Config();
        // 设置一个spout task上面最多有多少个没有处理的tuple（没有ack/failed）回复，以防止tuple队列爆掉。
        conf.setMaxSpoutPending(20);
        conf.setDebug(true);
        conf.put("redis-host", REDIS_HOST);
        conf.put("redis-port", REDIS_PORT);
        conf.put("redis-base", REDIS_BASE);
        conf.put("redis-auth", REDIS_AUTH);
        JedisPoolConfig poolConfig = new JedisPoolConfig.Builder().setHost(REDIS_HOST).setPort(REDIS_PORT)
                .setPassword(REDIS_AUTH).setDatabase(REDIS_BASE).build();
        if (args.length == 0) {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("risk-monitor", conf, buildTopology(poolConfig));
        } else if (args.length == 1) {
            conf.setNumWorkers(3);
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, buildTopology(poolConfig));
        } else if (args.length == 5) {
            conf.setNumWorkers(3);
            StormSubmitter.submitTopologyWithProgressBar(args[4], conf, buildTopology(poolConfig));
        } else {
            System.out.println(
                    "Usage: RiskMng1Topology <redis host> <redis port> <redis password> <redis database> (topology name)");
        }
    }
    
    public static StormTopology buildTopology(JedisPoolConfig config) {
        TopologyBuilder topology = new TopologyBuilder();
        RedisStoreMapper storeMapper = setupStoreMapper();
        RedisStoreBolt storeBolt = new RedisStoreBolt(config, storeMapper);
        topology.setSpout("redis-spout", new RiskMng4HorseSpout(RedisConfig.REDIS_SPOUT_KEY_NAME), 1);
        topology.setBolt("risk-bolt", new RiskMng1HorseBolt(), 1).fieldsGrouping("redis-spout", new Fields("ticket"));
        topology.setBolt("store-bolt", storeBolt, 1).shuffleGrouping("risk-bolt");
        return topology.createTopology();
    }
    
    private static RedisStoreMapper setupStoreMapper() {
        return new SalesCountStoreMapper();
    }
    private static class SalesCountStoreMapper implements RedisStoreMapper {
        private static final long serialVersionUID = -9134638277291238301L;
        
        private RedisDataTypeDescription description;
        
        public SalesCountStoreMapper() {
            description = new RedisDataTypeDescription(RedisDataTypeDescription.RedisDataType.HASH,
                    RedisConfig.REDIS_BOLT_KEY_NAME);
        }
        
        @Override
        public RedisDataTypeDescription getDataTypeDescription() {
            return description;
        }
        
        @Override
        public String getKeyFromTuple(ITuple tuple) {
            String key = tuple.getStringByField("id");
            return key;
        }
        
        @Override
        public String getValueFromTuple(ITuple tuple) {
            String value = tuple.getStringByField("result");
            return value;
        }
    }
}
