package storm.starter.bolt;

import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import twitter4j.Status;
import twitter4j.json.DataObjectFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * This abstract bolt class will help publishing the bolt processing into
 * a redis pubsub channel
 * @author arcturus@ardeenelinfierno.com
 *
 */
public abstract class RedisBolt implements IRichBolt {
	
	protected Jedis jedis;
	protected String channel;
	protected OutputCollector collector;
	protected Tuple currentTuple;
	
	public RedisBolt(String channel) {
		this.channel = channel;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		jedis = new Jedis("localhost");
		this.collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		currentTuple = tuple;
		List<Object> result = null;
		try {
			result = filter((Status)tuple.getValue(0));
		} catch(ClassCastException e) {
			result = filter(null);
		}
		if(result != null) {
			for(Object obj: result) {
				collector.emit(tuple, new Values(obj));				
			}
			collector.ack(tuple);
		}
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(channel));

	}
	
	public abstract List<Object> filter(Status status);
	
	public void publish(String msg) {
		jedis.publish(channel, msg);
	}

}
