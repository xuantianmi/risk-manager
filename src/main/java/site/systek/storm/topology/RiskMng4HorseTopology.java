package site.systek.storm.topology;

import java.util.ArrayList;
import java.util.List;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.LinearDRPCTopologyBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.systek.pojo.HorseRisk;
import site.systek.pojo.HorseRiskRequest;
import site.systek.pojo.HorseRiskResponse;
import site.systek.storm.bolt.RiskMng4HorseBolt;

/**
 * Risk Management for High frequecy fixed horse.
 * 
 * @author Merlin
 *
 */
public class RiskMng4HorseTopology {
    /**
     * storm kill hosre-risk
     * storm jar risk-manager-0.0.1.jar site.systek.storm.topology.RiskMng4HorseTopology horse-risk
     * 1. Call real cluster drpc
     * DRPCClient client = new DRPCClient("101.201.120.51", 3772);
     * String result = client.execute("exclamation","test");
     * 2. Call local drpc
     * LocalDRPC drpc = new LocalDRPC();
     * drpc.execute("exclamation", word);
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        LinearDRPCTopologyBuilder builder = new LinearDRPCTopologyBuilder("exclamation");
        builder.addBolt(new RiskMng4HorseBolt(), 1);
        Config conf = new Config();
        // WARN: 当设置为true时，每次从Spout或者Bolt发送元组，Storm都会写进日志，这对于调试程序是非常有用的。
        conf.setDebug(false);
        if (args != null && args.length > 0) {
            conf.setNumWorkers(3);
            conf.setDebug(false);
            // Send the Topology to real storm cluster.
            StormSubmitter.submitTopology(args[0], conf, builder.createRemoteTopology());
        } else {
            // Run the Topology on your own local computer.
            conf.setMaxTaskParallelism(3);
            conf.put(Config.NIMBUS_THRIFT_MAX_BUFFER_SIZE, 1048576);
            LocalDRPC drpc = new LocalDRPC();
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("horse-risk-drpc", conf, builder.createLocalTopology(drpc));
            ObjectMapper jacksonMapper = new ObjectMapper();
            for (int i = 1; i <= 1; i++) {
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
                System.out.println(riskResponse.getHorseRiskInfo().size());
                System.out.println(riskResponse.getHorseRiskInfo().get("1").getAward());
            }
            // 关闭一个本地集群
            cluster.shutdown();
            drpc.shutdown();
        }
    }
}
