/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.starter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.ShellBolt;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This topology demonstrates Storm's stream groupings and multilang capabilities.
 */
public class WordCountKafkaTopology {
	
	private static final Logger LOG = LoggerFactory.getLogger(
			WordCountKafkaTopology.class);
	public static final long OUTPUT_DURATION = 6000;
	private static long outputTime;

	public static void main(String[] args)
			throws Exception {
		SpoutConfig spoutConf = createConfig();
		
		if(args != null && args.length > 1
				&& args[1].equalsIgnoreCase("rich")){
			startTopology(args, spoutConf);
		}else{
			startBasicTopology(args, spoutConf);
		}
		
		System.out.println("OK! Startup.");
	}

	private static SpoutConfig createConfig() {
		String zks = "192.168.1.169:2181,"
				+ "192.168.1.179:2181,"
				+ "192.168.1.180:2181";
		String topic ="test_topic";
//	      String zkRoot ="/qianbao/kafka_2.12_1.0.0/logtest";
		String zkRoot = "/qianbao/kafka/logtest";
		String zkBrokersPath = zkRoot + "/brokers";
		// 读取的status会被存在，/zkRoot/id下面，所以id类似consumer group
		String id = "consumers/testWordCountKafkaTopology";
		
		BrokerHosts brokerHosts = new ZkHosts(zks, zkBrokersPath);
		SpoutConfig spoutConf =
				new SpoutConfig(brokerHosts,topic,zkRoot,id);
		spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
		//	      spoutConf.forceFromStart = false;
		spoutConf.startOffsetTime =
				kafka.api.OffsetRequest.LatestTime();
		spoutConf.zkServers= Arrays.asList(new String[]{
				"192.168.1.169",
				"192.168.1.179",
				"192.168.1.180"});
		spoutConf.zkPort=2181;
		return spoutConf;
	}
	
	public static void startBasicTopology(
			String[] args, SpoutConfig spoutConf)
					throws AlreadyAliveException,
					InvalidTopologyException,
					AuthorizationException {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafka-spout", new KafkaSpout(spoutConf));
		builder.setBolt("word-split", new BasicKafkaWordSplitter()).shuffleGrouping("kafka-spout");
		builder.setBolt("word-count", new BasicWordCounter()).fieldsGrouping("word-split", new Fields("word"));
		Config conf = new Config();
		conf.setDebug(true);
		
		String name = WordCountKafkaTopology.class.getSimpleName();
		if (args != null && args.length > 0) {
			name = args[0];
		}
		conf.setNumWorkers(3);
		
		StormSubmitter.submitTopologyWithProgressBar(
				name, conf, builder.createTopology());
		
		System.out.println(
				"Starting basic kafka topology"
						+ " with progress bar and"
						+ " with name \""+name+"\".");
	}
	
	public static void startTopology(String[] args, SpoutConfig spoutConf)
			throws AlreadyAliveException, InvalidTopologyException, AuthorizationException, InterruptedException {
		TopologyBuilder builder = new TopologyBuilder();
		
		//	    builder.setSpout("spout", new RandomSentenceSpout(), 5);

//	    builder.setBolt("split", new SplitSentence(), 8).shuffleGrouping("spout");
//	    builder.setBolt("count", new WordCount(), 12).fieldsGrouping("split", new Fields("word"));
	    
	    builder.setSpout("kafka-reader", new KafkaSpout(spoutConf), 2);
	    builder.setBolt("word-splitter",new KafkaWordSplitter(),2)
	    .shuffleGrouping("kafka-reader");
	    builder.setBolt("word-counter",new WordCounter())
	    .fieldsGrouping("word-splitter",new Fields("word"));
	    
	    Config conf = new Config();
	    conf.setDebug(true);

	    if (args != null && args.length > 0) {
	      conf.setNumWorkers(3);

	      StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
	      
	      System.out.println("Starting rich kafka topology"
	      		+ " with progress bar.");
	    }
	    else {
	      conf.setMaxTaskParallelism(3);

	      LocalCluster cluster = new LocalCluster();
	      cluster.submitTopology("word-count", conf, builder.createTopology());

	      Thread.sleep(60000);

	      cluster.shutdown();
	      
	      System.out.println("Starting local cluster.");
	    }
	}

  public static class KafkaWordSplitter extends BaseRichBolt{  
      // private static final Log LOG = LogFactory.getLog(KafkaWordSplitter.class);  
       
       private static final long serialVersionUID = 1L;
       private OutputCollector collector;
  
  
       @Override
       public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {  
           this.collector = collector;  
       }  
  
       @Override  
       public void execute(Tuple input) {  
           String line = input.getString(0);  
           LOG.info("RECE[kafka -> splitter] "+line);
           String[] words = line.split("\\s+");
           for(String word : words){
        	   if(word.indexOf(':')<0){
        		   LOG.info("EMIT[splitter -> counter] "+word);
        		   collector.emit(input,new Values(word,1));
        	   }
           }
           collector.ack(input);
       }
  
       @Override  
       public void declareOutputFields(OutputFieldsDeclarer declarer) {  
            declarer.declare(new Fields("word","count"));  
       }  
   }
  
  public static class WordCounter extends BaseRichBolt {  
       private static final long serialVersionUID =1L;  
       private OutputCollector collector;  
       private Map<String,AtomicInteger> counterMap;  
 
       @Override  
       public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {  
           this.collector=collector;  
           this.counterMap = new HashMap<String,AtomicInteger>();  
       }  
 
       @Override  
       public void execute(Tuple input) {  
           String word = input.getString(0);  
           int count = input.getInteger(1);  
           LOG.info("RECE[splitter -> counter] "+word+" : "+count);
           AtomicInteger ai = this.counterMap.get(word);
           if(ai==null){
               ai= new AtomicInteger();
               this.counterMap.put(word,ai);
           }
           ai.addAndGet(count);
           collector.ack(input);
           if(System.currentTimeMillis()-outputTime
        		   > OUTPUT_DURATION){
        	   LOG.info("CHECK statistics map: "+this.counterMap);
        	   outputTime = System.currentTimeMillis();
           }
       }
       
       @Override  
       public void declareOutputFields(OutputFieldsDeclarer declarer) {  
           declarer.declare(new Fields("word","count"));  
       }  
 
       @Override  
       public void cleanup() {  
           LOG.info("The final result:");  
           Iterator<Map.Entry<String,AtomicInteger>> iter = this.counterMap.entrySet().iterator();  
           while(iter.hasNext()){  
               Map.Entry<String,AtomicInteger> entry =iter.next();  
               LOG.info(entry.getKey()+"\t:\t"+entry.getValue().get());  
           }
       }
   }
  
  public static class BasicKafkaWordSplitter extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
//		private OutputCollector collector;
		public void execute(Tuple input, BasicOutputCollector collector) {
			String line = input.getString(0);  
	           LOG.info("RECE[kafka -> splitter] "+line);
	           String[] words = line.split("\\s+");
	           for(String word : words){
	        	   if(word.indexOf(':')<0){
	        		   LOG.info("EMIT[splitter -> counter] "+word);
	        		   collector.emit(new Values(word,1));
	        	   }
	           }
//	           collector.ack(input);
		}
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word", "count"));
		}
	}
  
  public static class BasicWordCounter extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
		private OutputCollector collector;
		private Map<String, AtomicInteger> counterMap;
		@Override
		public void prepare(Map stormConf, TopologyContext context) {
			counterMap = new HashMap<String, AtomicInteger>();
		}
		@Override
		public void cleanup() {
			LOG.info("The final result:");  
	           Iterator<Map.Entry<String,AtomicInteger>> iter = this.counterMap.entrySet().iterator();  
	           while(iter.hasNext()){  
	               Map.Entry<String,AtomicInteger> entry =iter.next();  
	               LOG.info(entry.getKey()+"\t:\t"+entry.getValue().get());  
	           }
		}
		public void execute(Tuple input, BasicOutputCollector collector) {
			String word = input.getString(0);  
	           int count = input.getInteger(1);  
//	           LOG.info("RECE[splitter -> counter] "+word+" : "+count);
	           AtomicInteger ai = this.counterMap.get(word);
	           if(ai==null){
	               ai= new AtomicInteger();
	               this.counterMap.put(word,ai);
	           }
	           ai.addAndGet(count);
//	           collector.ack(input);
	           if(System.currentTimeMillis()-outputTime
	        		   > OUTPUT_DURATION){
	        	   LOG.info("CHECK statistics map: "+this.counterMap);
	        	   outputTime = System.currentTimeMillis();
	           }
		}
		public void declareOutputFields(OutputFieldsDeclarer declarer) {}
	}
}
