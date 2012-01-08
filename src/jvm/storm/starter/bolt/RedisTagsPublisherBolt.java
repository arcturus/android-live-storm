package storm.starter.bolt;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import storm.starter.bolt.RedisBolt.OnDynamicConfigurationListener;
import storm.starter.utils.Utils;
import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 * 
 * Publish the hashtags coming from the different tweets.
 * 
 * @author arcturus@ardeenelinfierno.com
 *
 */
public class RedisTagsPublisherBolt extends RedisBolt implements OnDynamicConfigurationListener{
	
	private final List<String> forbiddenTags = new LinkedList<String>();

	public RedisTagsPublisherBolt(String channel) {
		super(channel);
	}
	
	@Override
	protected void setupNonSerializableAttributes() {
		super.setupNonSerializableAttributes();
		setupDynamicConfiguration(this);
	}

	@Override
	public List<Object> filter(Status status) {
		if(status == null) {
			return null;
		}
		
		List<HashtagEntity> hashtags = Arrays.asList(status.getHashtagEntities());
				
		if(hashtags != null && hashtags.size() > 0) {
			for(HashtagEntity tag: hashtags) {
				if(!forbiddenTags.contains(tag.getText().toLowerCase())) {
					publish(tag.getText().toLowerCase());
				}
			}
		}
		
		return null;
	}

	@Override
	public void onConfigurationChange(String conf) {
		Utils.StringToList(conf, forbiddenTags);
	}

}
