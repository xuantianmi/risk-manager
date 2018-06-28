package site.systek.storm.trident;

import java.io.IOException;
import java.util.HashMap;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.trident.state.RedisMapState;
import org.apache.storm.trident.TridentState;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.CombinerAggregator;
import org.apache.storm.trident.operation.Consumer;
import org.apache.storm.trident.operation.ReducerAggregator;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.builtin.MapGet;
import org.apache.storm.trident.state.StateFactory;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.systek.pojo.HorseRisk;
import site.systek.storm.spout.RiskMng4HorseSpout;

public class TridentRiskTopology {
	public final static String REDIS_HOST = "localhost"; // "192.169.200.16";
	public final static int REDIS_PORT = 6379;
	public final static String REDIS_SPOUT_KEY_NAME2 = "tickets4test";
	public final static String REDIS_STATE_KEY_NAME = "state4test";
	public final static int REDIS_DATABASE = 10;

	public static void main(String... args) throws Exception {
		Config conf = new Config();
		// 设置一个spout task上面最多有多少个没有处理的tuple（没有ack/failed）回复，以防止tuple队列爆掉。
		conf.setMaxSpoutPending(20);
		// conf.setDebug(true);

		conf.put("redis-host", REDIS_HOST);
		conf.put("redis-port", REDIS_PORT);

		if (args.length == 0) {
			System.out.println("Go: LocalCluster");
			LocalDRPC drpc = new LocalDRPC();
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("risk-monitor", conf, buildTopology(drpc));

			Thread.sleep(1000);
			for (int i = 0; i < 1; i++) {
				String result = drpc.execute("risk-monitor", "horse1 horse2 horse3 horse4 horse5");
				//String result = drpc.execute("risk-monitor", "horse1 horse2 horse3");
				System.out.println("DRPC RESULT: " + result);
			}
			cluster.killTopology("risk-monitor");
			cluster.shutdown();
			System.exit(0);

		} else {
			System.out.println("Go: RealCluster");
			conf.setNumWorkers(3);
			StormSubmitter.submitTopologyWithProgressBar(args[0], conf, buildTopology(null));
		}
	}

	@SuppressWarnings("serial")
	public static StormTopology buildTopology(LocalDRPC drpc) {
		TridentTopology topology = new TridentTopology();

		RiskMng4HorseSpout spout = new RiskMng4HorseSpout(REDIS_SPOUT_KEY_NAME2, REDIS_DATABASE);

		JedisPoolConfig poolConfig = new JedisPoolConfig.Builder().setHost(REDIS_HOST).setPort(REDIS_PORT)
				.setDatabase(REDIS_DATABASE).build();
		StateFactory redisState = RedisMapState.nonTransactional(poolConfig);

		TridentState saleCounts = topology.newStream("spout1", spout).parallelismHint(5)
				.each(new Fields("ticket"), new SplitTicket(), new Fields("horseID", "volume"))
				.groupBy(new Fields("horseID")).persistentAggregate(redisState, new Fields("horseID", "volume"),
						new VolumeCount(), new Fields("count"))
				.parallelismHint(5);

		topology.newDRPCStream("risk-monitor", drpc).each(new Fields("args"), new Split(), new Fields("horse"))
				.peek(new Consumer() {
					@Override
					public void accept(TridentTuple input) {
						System.out.println("Peek args: " + input.getString(0));
					}
				}).groupBy(new Fields("horse"))
				.stateQuery(saleCounts, new Fields("horse"), new MapGet(), new Fields("count"))
				.partitionAggregate(new Fields("horse", "count"), new SumHorse(), new Fields("sum"));

		return topology.build();
	}

	private static class Split extends BaseFunction {
		private static final long serialVersionUID = -4381417656111121586L;

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			String sentence = tuple.getString(0);
			for (String word : sentence.split(" ")) {
				collector.emit(new Values(word));
			}
		}
	}

	private static class SplitTicket extends BaseFunction {
		private static final long serialVersionUID = 1107381581429696567L;

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			String jsonStr = tuple.getString(0);
			ObjectMapper jacksonMapper = new ObjectMapper();

			try {
				HorseRisk ticket = jacksonMapper.readValue(jsonStr, HorseRisk.class);
				collector.emit(new Values("horse" + ticket.getId().toString(), ticket.getVolume().toString()));
			} catch (IOException e) {
				System.err.println("HorseRisk:" + jsonStr);
				e.printStackTrace();
			}

		}
	}

	private static class VolumeCount implements CombinerAggregator<Long> {
		private static final long serialVersionUID = 6543290930447378833L;

		@Override
		public Long init(TridentTuple tuple) {
			Object tmp = tuple.get(1);
			return Long.parseLong(tmp.toString());
		}

		@Override
		public Long combine(Long val1, Long val2) {
			return val1 + val2;
		}

		@Override
		public Long zero() {
			return 0L;
		}

	}

	private static class SumHorse implements ReducerAggregator<HashMap<String, Integer>> {
		private static final long serialVersionUID = -643940840037611244L;

		@Override
		public HashMap<String, Integer> init() {
			return new HashMap<String, Integer>();
		}

		@Override
		public HashMap<String, Integer> reduce(HashMap<String, Integer> curr, TridentTuple tuple) {
			String id = tuple.getValue(0).toString();
			Integer count = Integer.valueOf(tuple.getValue(1).toString());
			curr.put(id, count);
			return curr;
		}

	}

}
