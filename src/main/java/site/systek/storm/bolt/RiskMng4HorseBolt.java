package site.systek.storm.bolt;

import java.util.Hashtable;
import java.util.List;

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

import site.systek.pojo.HorseRisk;
import site.systek.pojo.HorseRiskRequest;
import site.systek.pojo.HorseRiskResponse;

public class RiskMng4HorseBolt extends BaseBasicBolt {
    
    public static Logger logger = LoggerFactory.getLogger(RiskMng4HorseBolt.class);
    
    /**
     * 
     */
    private static final long serialVersionUID = 1506331332291110088L;
    
    /**
     * 当前期奖期编号
     */
    public static String current_draw_id = "";
    
    private static Hashtable<String, HorseRisk> horseRiskInfo = new Hashtable<String, HorseRisk>();
    
    public void execute(Tuple input, BasicOutputCollector collector) {
        String out = "";
        HorseRiskRequest riskRequest = null;
        HorseRiskResponse riskResponse = null;
        try {
            String jsonStr = input.getString(1);
            logger.debug(jsonStr);
            // 使用jackson-json工具解析和封装
            ObjectMapper mapper = new ObjectMapper();
            riskRequest = mapper.readValue(jsonStr, HorseRiskRequest.class);
            riskResponse = new HorseRiskResponse(riskRequest.getDrawId(), false, null);
            // 传入的JSON串drawId不为空
            if (StringUtils.isNotBlank(riskRequest.getDrawId())) {
                // 不做计算只想得到统计结果
                if (StringUtils.isNotBlank(riskRequest.getOperation()) && riskRequest.getOperation().equals("get")) {
                    if (riskRequest.getDrawId().equals(current_draw_id)) {
                        riskResponse.setSuccess(true);
                        riskResponse.setHorseRiskInfo(horseRiskInfo);
                    } else {
                        riskResponse.setSuccess(true);
                        riskResponse.setHorseRiskInfo(null);
                    }
                } else {
                    // 统计各马匹的总销量和总返奖
                    if (current_draw_id.isEmpty()) {
                        current_draw_id = riskRequest.getDrawId();
                    } else {
                        // 开始了新的奖期，要重新开始计算
                        if (!current_draw_id.equals(riskRequest.getDrawId())) {
                            current_draw_id = riskRequest.getDrawId();
                            horseRiskInfo.clear();
                        }
                    }
                    List<HorseRisk> horseInfo = riskRequest.getHorseInfo();
                    for (HorseRisk horse : horseInfo) {
                        int roleId = horse.getId();
                        int volume = horse.getVolume();
                        int award = horse.getAward();
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
                        horseRisk.setVolume(horseRisk.getVolume() + volume);
                        horseRisk.setAward(horseRisk.getAward() + award);
                        horseRiskInfo.put(String.valueOf(roleId), horseRisk);
                    }
                }
                riskResponse.setSuccess(true);
            } else {
                riskResponse.setSuccess(false);
            }
            out = mapper.writeValueAsString(riskResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        collector.emit(new Values(input.getValue(0), out));
    }
    
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("id", "result"));
    }
}
