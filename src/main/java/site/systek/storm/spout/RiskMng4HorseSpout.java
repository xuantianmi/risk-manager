package site.systek.storm.spout;

import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import redis.clients.jedis.Jedis;

public class RiskMng4HorseSpout extends BaseRichSpout {
    private static final long serialVersionUID = 2736140010063230835L;
    
    private SpoutOutputCollector _collector;
    
    private Jedis jedis;
    
    private String host;
    
    private int port;
    
    private String redisKeyName;
    
    private int databasse = -1;
    
    private String auth;
    
    /**
     * 
     * @param redisKey redis queue key name
     */
    public RiskMng4HorseSpout(String redisKey) {
        this.redisKeyName = redisKey;
    }
    
    public RiskMng4HorseSpout(String redisKey, int database) {
        this.redisKeyName = redisKey;
        this.databasse = database;
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
        host = conf.get("redis-host").toString();
        port = Integer.valueOf(conf.get("redis-port").toString());
        auth = conf.get("redis-auth").toString();
        databasse = Integer.valueOf(conf.get("redis-base").toString());
        reconnect();
    }
    
    private void reconnect() {
        jedis = new Jedis(host, port);
        jedis.auth(auth);
        if (this.databasse > 0) {
            jedis.select(this.databasse);
        }
    }
    
    @Override
    public void nextTuple() {
        String content = jedis.lpop(this.redisKeyName);
        if (content == null || "nil".equals(content)) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
            }
        } else {
            _collector.emit(new Values(content));
        }
    }
    
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("ticket"));
    }
}
