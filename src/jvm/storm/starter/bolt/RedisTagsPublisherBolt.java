package storm.starter.bolt;

import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 * 
 * Publish the hashtags coming from the different tweets.
 * 
 * @author arcturus@ardeenelinfierno.com
 *
 */
public class RedisTagsPublisherBolt extends RedisBolt {

	public RedisTagsPublisherBolt(String channel) {
		super(channel);
	}

	@Override
	public List<Object> filter(Status status) {
		HashtagEntity hashtags[] = status.getHashtagEntities();
		
		if(hashtags != null && hashtags.length > 0) {
			for(HashtagEntity tag: hashtags) {
				publish(tag.getText().toLowerCase());
			}
		}
		
		return null;
	}

}
