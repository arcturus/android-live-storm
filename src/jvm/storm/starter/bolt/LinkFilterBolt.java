package storm.starter.bolt;

import java.util.Map;

import twitter4j.Status;
import twitter4j.URLEntity;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * Bolt that will emit those twitter status that have
 * urls on it.
 * @author arcturus@ardeenelinfierno.com
 *
 */
public class LinkFilterBolt implements IRichBolt {
	
	OutputCollector collector;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;

	}

	@Override
	public void execute(Tuple tuple) {
		Status status = (Status)tuple.getValue(0);
		URLEntity urls[] = status.getURLEntities();
		
		if(urls != null && urls.length > 0) {
			collector.emit(tuple, new Values(status));
			collector.ack(tuple);
		}

	}

	@Override
	public void cleanup() {

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweets"));

	}

}
