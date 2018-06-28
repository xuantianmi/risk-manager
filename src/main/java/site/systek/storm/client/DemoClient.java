package site.systek.storm.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import site.systek.pojo.BetType;
import site.systek.pojo.HorseRisk;
import site.systek.pojo.HorseRiskRequest;
import site.systek.pojo.Operation;
import site.systek.pojo.Stake;
import site.systek.storm.redis.RedisConfig;

public class DemoClient {
    
    private Jedis conn;
    
    public static void main(String[] args) {
        DemoClient client = new DemoClient();
        client.insertRedisData4Horse1();
    }
    
    public void insertRedisData4Horse1() {
        conn = new Jedis(RedisConfig.REDIS_HOST, RedisConfig.REDIS_PORT);
        conn.auth(RedisConfig.REDIS_AUTH);
        conn.select(RedisConfig.REDIS_BASE); // 选择Redis数据库
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 1; i <= 50; i++) {
            Stake stake = new Stake();
            stake.setAccountId(String.valueOf(i));
            stake.setDrawId("10000");
            stake.setBetType(BetType.STAKE.getTypeCode());
            stake.setPlayId(1);
            //
            String betInfo = "";
            int floop = ThreadLocalRandom.current().nextInt(1, 6);
            for (int j = 1; j <= floop; j++) {
                String info = j + "x100";
                betInfo = j == 1 ? betInfo + info : betInfo + "#" + info;
            }
            stake.setBetInfo(betInfo);
            String input;
            try {
                input = mapper.writeValueAsString(stake);
                conn.rpush(RedisConfig.REDIS_SPOUT_KEY_NAME, input);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void insertRedisData4Horse2() {
        conn = new Jedis(RedisConfig.REDIS_HOST, RedisConfig.REDIS_PORT);
        conn.auth(RedisConfig.REDIS_AUTH);
        conn.select(RedisConfig.REDIS_BASE); // 选择Redis数据库
        ObjectMapper mapper = new ObjectMapper();
        for (int i = 1; i <= 5; i++) {
            HorseRiskRequest riskRequest = new HorseRiskRequest("1000", Operation.ADD.getCode(), null);
            List<HorseRisk> horseInfo = new ArrayList<>();
            for (int j = 1; j <= 5; j++) {
                HorseRisk horse = new HorseRisk(i, j * 100, j * 200);
                horseInfo.add(horse);
            }
            riskRequest.setHorseInfo(horseInfo);
            String input;
            try {
                input = mapper.writeValueAsString(riskRequest);
                conn.rpush(RedisConfig.REDIS_SPOUT_KEY_NAME, input);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
