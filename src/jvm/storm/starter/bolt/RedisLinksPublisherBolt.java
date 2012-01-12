package storm.starter.bolt;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.zookeeper.proto.SyncRequest;
import org.json.simple.JSONObject;

import storm.starter.bolt.RedisBolt.OnDynamicConfigurationListener;
import storm.starter.utils.Utils;
import twitter4j.Status;
import twitter4j.URLEntity;

/**
 * 
 * Bolt for publishing into a redis channel the results
 * for those tweets that contains a link.
 * 
 * Will try to expand the url if it's shorten.
 * 
 * @author arcturus@ardeenelinfierno.com
 *
 */
public class RedisLinksPublisherBolt extends RedisBolt implements OnDynamicConfigurationListener{
	
	public static final String CHANNEL = "links";
	private final List<String> forbiddenDomains = new LinkedList<String>();

	public RedisLinksPublisherBolt() {
		super(CHANNEL);
	}
	
	@Override
	protected void setupNonSerializableAttributes() {
		// TODO Auto-generated method stub
		super.setupNonSerializableAttributes();
		setupDynamicConfiguration(this);
	}

	@Override
	public List<Object> filter(Status status) {
		URLEntity urls[] = status.getURLEntities();
		
		if(urls == null) {
			return null;
		}
		
		URL finalUrl = null;
		List<Object> marketUrls = new LinkedList<Object>();
		for(URLEntity url: urls) {
			finalUrl = getFinalUrl(url.getURL());
			
			if(finalUrl == null) {
				continue;
			}
			
			String extra = null;
			if(!url.getURL().toString().equals(finalUrl.toString())) {
				extra = ", \"original\": \"" + url.getURL().toString() + "\"";				
			}
			
			JSONObject msg = new JSONObject();
			msg.put("link", finalUrl.toString());
			msg.put("host", finalUrl.getHost());
			if(extra != null) {
				msg.put("original", url.getURL().toString());
			}
			
			publish(msg.toJSONString());
			
			if("market.android.com".equals(finalUrl.getHost()) && finalUrl.getPath().contains("details")) {
				marketUrls.add(msg.toJSONString());
			}
		}
		
		
		return marketUrls.size() == 0 ? null : marketUrls;

	}
	
	private URL getFinalUrl(URL url, int deep) {
		if(url == null) {
			return null;
		}
		
		if(deep <= 0) {
			return url;
		}
		
		synchronized (forbiddenDomains) {
			if(forbiddenDomains.contains(url.getHost())) {
				log.debug("Forbidden link: " + url.toString());
				return null;
			}
		}
		
		if(url.toString().length() > 30) {
			return url;
		}
		
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setInstanceFollowRedirects(false);
			String location = connection.getHeaderField("Location");
			if(location != null && location.length() > 0) {
				URL newURL = new URL(location);
				return getFinalUrl(newURL, deep - 1);
			}
		} catch (IOException e) {
			return url;
		}
		
		return url;
	}
	
	private URL getFinalUrl(URL url) {
		return getFinalUrl(url, 5);
	}
	
	public void onConfigurationChange(String conf) {
		Utils.StringToList(conf, forbiddenDomains);
	}
	
}
