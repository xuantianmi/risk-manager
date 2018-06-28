package site.systek.storm.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.storm.Config;
import org.apache.storm.utils.DRPCClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.systek.pojo.HorseRisk;
import site.systek.pojo.HorseRiskRequest;
import site.systek.pojo.HorseRiskResponse;

public class StormDRPCient {
    public static void main(String[] args) throws Exception {
        Config conf = new Config();
        // Ref: http://storm.apache.org/releases/current/javadocs/org/apache/storm/Config.html
        conf.put(Config.STORM_THRIFT_TRANSPORT_PLUGIN, "org.apache.storm.security.auth.SimpleTransportPlugin");
        conf.put(Config.STORM_NIMBUS_RETRY_TIMES, 5);
        conf.put(Config.STORM_NIMBUS_RETRY_INTERVAL, 2000);
        conf.put(Config.STORM_NIMBUS_RETRY_INTERVAL_CEILING, 60000);
        conf.put(Config.NIMBUS_THRIFT_MAX_BUFFER_SIZE, 1048576);
        conf.put(Config.DRPC_MAX_BUFFER_SIZE, 1048576);
        conf.put(Config.DRPC_REQUEST_TIMEOUT_SECS, 600);
        // 当设置为true时，每次从Spout或者Bolt发送元组，Storm都会写进日志
        // conf.setDebug(true);
        // conf.setNumWorkers(3);
        // DRPCClient client = new DRPCClient(conf, "storm.systek.site", 3772, 600); // 101.201.120.51
        // DRPCClient client = new DRPCClient(conf, "101.201.70.83", 3772, 600);
        DRPCClient drpc = new DRPCClient(conf, "storm.systek.site", 3772, 600); // 101.201.62.28
        ObjectMapper jacksonMapper = new ObjectMapper();
        for (int i = 1; i <= 5; i++) {
            HorseRiskRequest riskRequest = new HorseRiskRequest("1000", null, null);
            List<HorseRisk> horseInfo = new ArrayList<>();
            for (int j = 1; j <= 5; j++) {
                HorseRisk horse = new HorseRisk(i, j * 100, j * 200);
                horseInfo.add(horse);
            }
            riskRequest.setHorseInfo(horseInfo);
            String input = jacksonMapper.writeValueAsString(riskRequest);
            String out = drpc.execute("exclamation", input.toString());
            System.out.println("------------reqt for drpc " + input + " \n------------resp frm drpc " + out);
            riskRequest.setOperation("get");
            riskRequest.setHorseInfo(null);
            input = jacksonMapper.writeValueAsString(riskRequest);
            out = drpc.execute("exclamation", input.toString());
            System.out.println("------------reqt for drpc " + input + " \n------------resp frm drpc " + out);
            HorseRiskResponse riskResponse = jacksonMapper.readValue(out, HorseRiskResponse.class);
            System.out.println("------------"+riskResponse.getHorseRiskInfo().size());
            System.out.println("------------"+riskResponse.getHorseRiskInfo().get("1").getAward());
        }
    }
}
