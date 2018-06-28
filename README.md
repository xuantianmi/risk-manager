# 基于*Storm*的风险控制模型

## 调研三种基于Storm的模型:
* **DRPC** only  
*ref: site.systek.storm.topology.RiskMng4HorseTopology*  
*DRPC负责请求的输入输出,客户端通过RPC方式调用Storm. 由于DRPC连接数限制（测试时没个Storm节点可承接约100个DRPC连接），不能实现高并发处理。*
* Redis Spout+Bolt+ Redis Bolt  
*ref: site.systek.storm.topology.RiskMng1Topology*  
*将待处理数据放到Redis队列中，通过Spout读取Redis数据，最终通过Bolt将分析结果写／更新到新的Redis队列中。*
* **Trident**DRPC, TridentState--RedisSpout+RedisMapState持久化  
*ref: site.systek.storm.trident.TridentRiskTopology*  
*将待处理数据放到Redis队列中，通过TridentState获取原数据（Redis）并持久化阶段性分析数据（Redis）。DRPC调用时，可以从TridentState中获取分析数据（也可以进一步分析处理）并返回。*   
建议：采用Trident方式用于实际应用。
  
## Storm集群化安装部署
>参考：Install Storm Cluster on CentOS in Aliyun
