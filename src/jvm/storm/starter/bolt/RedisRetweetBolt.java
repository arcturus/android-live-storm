package storm.starter.bolt;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import twitter4j.Status;

/**
 * 
 * Bolt that will publish to a redis channel those tweets
 * that have been retweeted a number of times.
 * 
 * Can be moved this bolt to any other that alread works with the tweets
 * but, this is just a test :P
 * @author arcturus@ardeenelinfierno.com
 *
 */
public class RedisRetweetBolt extends RedisBolt {
	
	public static final String CHANNEL = "retweets";
	protected int retweetCount;
	
	public RedisRetweetBolt(int count) {
		super(CHANNEL);
		retweetCount = count;
	}

	@Override
	public List<Object> filter(Status status) {
		if(status.getRetweetCount() < retweetCount) {
			return null;
		}
		
		//Just tweets two days old
		if(status.getCreatedAt().getTime() + 2*24*60*60*1000 < System.currentTimeMillis()) {
			return null;
		}
		
		JSONObject msg = new JSONObject();
		Status originalStatus = status.getRetweetedStatus();
		msg.put("user", originalStatus.getUser().getScreenName());
		msg.put("photo", originalStatus.getUser().getProfileImageURL().toString());
		msg.put("tweet", originalStatus.getText());
		msg.put("id", originalStatus.getId());
		msg.put("count", status.getRetweetCount() > 100 ? "> 100" : status.getRetweetCount());
		
		publish(msg.toJSONString());		
		
		List<Object> result = new ArrayList<Object>();
		result.add(status);
		return result;
	}

}
