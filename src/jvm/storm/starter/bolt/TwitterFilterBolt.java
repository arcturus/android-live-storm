package storm.starter.bolt;

import java.util.Map;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class TwitterFilterBolt implements IRichBolt {

	OutputCollector collector;

	@Override
	public void execute(Tuple tuple) {
		Status status = (Status)tuple.getValue(0);
		
		HashtagEntity hashTags[] = status.getHashtagEntities();
		if(hashTags != null && hashTags.length >=2) {
			int count = 0;
			/**
			 * Do simple filtering to remove spam tweets
			 */
			for(HashtagEntity hashtag: hashTags) {
				if("android".equals(hashtag.getText().toLowerCase()) ||
						"androidgames".equals(hashtag.getText().toLowerCase())) {
					count++;
				}
			}
			
			if(count == 2) {
				return;
			}
			
		}
		
		collector.emit(tuple, new Values(status));
		collector.ack(tuple);
	}

	@Override
	public void cleanup() {

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("FJJ"));
	}


	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		
	}

}
