package storm.starter;

import storm.starter.bolt.LinkFilterBolt;
import storm.starter.bolt.RedisGooseExtractor;
import storm.starter.bolt.RedisLinksPublisherBolt;
import storm.starter.bolt.RedisMarketBolt;
import storm.starter.bolt.RedisRetweetBolt;
import storm.starter.bolt.RedisTagsPublisherBolt;
import storm.starter.bolt.TwitterFilterBolt;
import storm.starter.spout.TwitterSampleSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

public class TwitterTopology {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		TopologyBuilder builder = new TopologyBuilder();
				
		//Tweets from twitter sport
		//TODO: setup your twitter credentials
		TwitterSampleSpout twitterSpout = new TwitterSampleSpout("o2labstest", "o2labs");
		builder.setSpout("twitter", twitterSpout);
		
		//Initial filter
		builder.setBolt("filter", new TwitterFilterBolt(), 2).shuffleGrouping("twitter");
		
		//Tags publishing
		builder.setBolt("tags", new RedisTagsPublisherBolt("tags")).shuffleGrouping("filter");
		
		//Retweets
		builder.setBolt("retweets", new RedisRetweetBolt(3), 2).shuffleGrouping("filter");
		
		//Links
		builder.setBolt("linkFilter", new LinkFilterBolt(), 2).shuffleGrouping("filter");
		builder.setBolt("links", new RedisLinksPublisherBolt(), 4).shuffleGrouping("linkFilter");
		builder.setBolt("market", new RedisMarketBolt(), 1).shuffleGrouping("links");
		builder.setBolt("articles", new RedisGooseExtractor(), 5).shuffleGrouping("retweets");
		
		
		Config conf = new Config();
        conf.setDebug(false);
        
        if(args!=null && args.length > 0) {
            conf.setNumWorkers(3);
            
            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        } else {
	        LocalCluster cluster = new LocalCluster();
	        cluster.submitTopology("twitter", conf, builder.createTopology());
        }

	}

}
