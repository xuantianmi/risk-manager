package site.systek.storm.bolt;

import java.util.Hashtable;

import org.apache.commons.lang.StringUtils;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.systek.pojo.BetType;
import site.systek.pojo.HorseRisk;
import site.systek.pojo.HorseRiskResponse;
import site.systek.pojo.Stake;

public class RiskMng1HorseBolt extends BaseBasicBolt {
    
    private static final long serialVersionUID = -9066668066359602984L;
    
    public static Logger LOGGER = LoggerFactory.getLogger(RiskMng1HorseBolt.class);
    
    public static String current_draw_id = "";
    
    private static Hashtable<String, HorseRisk> horseRiskInfo = new Hashtable<String, HorseRisk>();
    
    private static final String MULTIPLE_HORSE_PREFIX = "#";
    
    private static final String SINGLE_HORSE_PREFIX = "x";
    
    public void execute(Tuple input, BasicOutputCollector collector) {
        int tupleSize = input.size();
        String out = "";
        Stake stake = null;
        HorseRiskResponse riskResponse = null;
        try {
            String jsonStr = tupleSize == 1 ? input.getString(0) : input.getString(1);
            System.out.println(jsonStr);
            // 使用jackson-json工具解析和封装
            ObjectMapper mapper = new ObjectMapper();
            stake = mapper.readValue(jsonStr, Stake.class);
            // 传入的JSON串drawId不为空
            if (StringUtils.isNotBlank(stake.getDrawId())) {
                riskResponse = new HorseRiskResponse(stake.getDrawId(), false, null);
                if (stake.getBetType() != null && stake.getBetType() > 0) {
                    // 统计各马匹的总销量和总返奖
                    if (current_draw_id.isEmpty()) {
                        current_draw_id = stake.getDrawId();
                    } else {
                        // 开始了新的奖期，要重新开始计算
                        if (!current_draw_id.equals(stake.getDrawId())) {
                            current_draw_id = stake.getDrawId();
                            horseRiskInfo.clear();
                        }
                    }
                    if (StringUtils.isNotBlank(stake.getBetInfo())) {
                        String[] betInfos = stake.getBetInfo().split(MULTIPLE_HORSE_PREFIX);
                        for (String betInfo : betInfos) {
                            String[] info = betInfo.split(SINGLE_HORSE_PREFIX);
                            int roleId = Integer.parseInt(info[0]);
                            int volume = Integer.parseInt(info[1]);
                            int award = 0;
                            // 累加销量和返奖
                            HorseRisk horseRisk = horseRiskInfo.get(String.valueOf(roleId));
                            if (horseRisk == null) {
                                // 没有马匹的信息，要初始化
                                horseRisk = new HorseRisk();
                                horseRisk.setId(roleId);
                                horseRisk.setVolume(0);
                                horseRisk.setAward(0);
                            }
                            // 计算总销量和返奖金额
                            if (stake.getBetType() == BetType.STAKE.getTypeCode()) {
                                horseRisk.setVolume(horseRisk.getVolume() + Math.abs(volume));
                                horseRisk.setAward(horseRisk.getAward() + Math.abs(award));
                            } else if (stake.getBetType() == BetType.CANCEL.getTypeCode()) {
                                horseRisk.setVolume(horseRisk.getVolume() - Math.abs(volume));
                                horseRisk.setAward(horseRisk.getAward() - Math.abs(award));
                            }
                            horseRiskInfo.put(String.valueOf(roleId), horseRisk);
                        }
                    }
                }
                riskResponse.setSuccess(true);
                riskResponse.setHorseRiskInfo(horseRiskInfo);
                out = mapper.writeValueAsString(riskResponse);
                collector.emit(new Values(current_draw_id, out));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("统计销量服务出现异常", e);
            if (StringUtils.isNotBlank(current_draw_id)) {
                riskResponse.setSuccess(false);
                collector.emit(new Values(current_draw_id, out));
            }
        }
    }
    
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "result"));
    }
}
