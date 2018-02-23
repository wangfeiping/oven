### 部署Storm 单机集群

参考：

> (官方) https://hub.docker.com/_/storm/

> https://github.com/kubernetes/examples/tree/master/staging/storm

> https://www.jianshu.com/p/74199e8a80f2

> https://community.hortonworks.com/articles/36151/debugging-an-apache-storm-topology.html

> https://yq.aliyun.com/articles/47036

### 单机docker 集群部署

```
docker run -d \
    --name zk \
    -p 2181:2181 \
    mattf/zookeeper
```

```
docker run -d --restart always \
  -v /home/vagrant/storm/:/logs \
  --name nimbus -p 6627:6627 --link zk:mattf/zookeeper storm storm nimbus
```

```
docker run -d --restart always --name supervisor \
  -v /home/vagrant/storm/:/logs \
  --link zk:mattf/zookeeper --link nimbus:nimbus storm storm supervisor
```

```
docker run --link nimbus:nimbus -it --rm -v $(pwd)/storm-starter-1.1.1.jar:/topology.jar storm storm jar /topology.jar org.apache.storm.starter.WordCountTopology topology
```

```
docker run --link nimbus:nimbus -it --rm -v $(pwd)/storm-starter-1.1.1.jar:/topology.jar storm storm jar /topology.jar org.apache.storm.starter.WordCountKafkaTopology topology rich
```

Storm UI (可选)

```
docker run -d -p 8080:8080 --restart always --name ui --link nimbus:nimbus storm storm ui
```

### topology 开发

参考：

> （官方） https://github.com/apache/storm/tree/master/examples/storm-starter

> http://nathanmarz.github.io/storm/doc/backtype/storm/topology/TopologyBuilder.html

> http://www.haroldnguyen.com/blog/2015/01/setting-up-storm-and-running-your-first-topology/

> https://docs.microsoft.com/en-us/azure/hdinsight/storm/apache-storm-develop-java-topology

> https://stackoverflow.com/questions/20880711/how-to-create-a-topology-in-storm

```

git clone https://github.com/apache/storm.git

git checkout v1.1.1

```

### 部署Storm 跨主机集群部署

参考：  

> http://www.cnblogs.com/panfeng412/archive/2012/11/30/how-to-install-and-deploy-storm-cluster.html

> http://storm.apache.org/releases/current/Setting-up-a-Storm-cluster.html

### Storm 数据输入以及任务计算快照结果输出

参考：  

> (官方文档 ) http://storm.apache.org/about/simple-api.html

> (官方文档) http://nathanmarz.github.io/storm/doc/overview-summary.html

> http://blog.csdn.net/jmppok/article/details/17284817

> http://blog.csdn.net/cuihaolong/article/details/52684396

> http://blog.csdn.net/xianzhen376/article/details/53409707

> http://blog.csdn.net/ch717828/article/details/50748912

> (美团：Storm 的可靠性保证测试) https://tech.meituan.com/test-of-storms-reliability.html

> http://www.cnblogs.com/cruze/p/4241181.html

> https://storm.apache.org/releases/1.1.1/storm-kafka.html

> https://basebase.github.io/2016/08/11/storm%E6%95%B4%E5%90%88kafka%E9%87%8D%E5%A4%8D%E6%B6%88%E8%B4%B9%E9%97%AE%E9%A2%98%E5%88%86%E6%9E%90/

### 集群部署 注意：如果重启，需要清理zookeeper 中的数据，否则可能造成启动失败！

```
docker run -d --restart always --name nimbus \
  --net=host \
  -v /apps/logs/:/logs storm \
  storm nimbus \
  -c storm.zookeeper.servers='["192.168.1.169","192.168.1.179","192.168.1.180"]'
```

```
docker run -d --restart always --name supervisor \
  --net=host \
  -v /apps/logs/:/logs storm \
  storm supervisor \
  -c nimbus.seeds='["172.28.32.202"]' \
  -c storm.zookeeper.servers='["192.168.1.169","192.168.1.179","192.168.1.180"]'
```

```
docker run -d --restart always --name ui \
  --net=host \
  -v /apps/logs/:/logs storm \
  storm ui -c nimbus.seeds='["172.28.32.202"]' \
  -c storm.zookeeper.servers='["192.168.1.169","192.168.1.179","192.168.1.180"]'
```

注意：因为执行topology 的过程中发现topo 总是重启，多次测试后发现是因为内存溢出，估计是由于读取kafka 数据比解析统计数据快得多，导致占用大量内存，因此使用如下参数，尝试解决此问题：

> topology.max.spout.pending=1

```
项目源码，基于该项目开发
https://github.com/apache/storm/tree/master/examples/storm-starter

docker run -it --rm -v $(pwd)/storm-starter-1.1.1.0.jar:/topology.jar storm \
  storm \
  -c nimbus.seeds='["172.28.32.202"]' \
  -c storm.zookeeper.servers='["192.168.1.169","192.168.1.179","192.168.1.180"]' \
  -c topology.max.spout.pending=1 \
  jar /topology.jar org.apache.storm.starter.WordCountKafkaTopology topology rich
```

```
项目源码
https://github.com/apache/storm/tree/master/examples/storm-kafka-examples

docker run -it --rm \
  -v $(pwd)/storm-kafka-examples-1.1.1.jar:/topology.jar storm \
  storm \
  -c nimbus.seeds='["172.28.32.202"]' \
  -c storm.zookeeper.servers='["192.168.1.169","192.168.1.179","192.168.1.180"]' \
  -c topology.max.spout.pending=1 \
  jar /topology.jar org.apache.storm.kafka.trident.TridentKafkaWordCount
```

### 附录：zk 操作
```
bin/zkCli.sh -server 192.168.1.169:2181,192.168.1.179:2181,192.168.1.180:2181
> ls2 /qianbao 
> create /qianbao/kafka_002 ""
> delete /qianbao/kafka/test
> quit
```

### 几个疑问

> 每次重启storm 需要清空或重启zk？

> 每次topo 异常重启kafka 如何从zk 恢复？可能是strom 自身有kafka 消费失败回滚机制？任务正常重启似乎不会重新消费？

> 递归逻辑？

> topo 最终的计算结果是否可以通过分布式实现？比如，通过kafka 消费（可分布式），分布式的计算局部统计数据，然后输出到kafka 或influxdb 或tidb，最终总的统计结果通过查询计算？
