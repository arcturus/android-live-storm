package storm.starter.bolt;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import storm.starter.bolt.RedisBolt.OnDynamicConfigurationListener;
import storm.starter.utils.Utils;
import twitter4j.Status;

/**
 * 
 * This bolt will get an url coming from the android market
 * and will try to get the html and parse the information, 
 * publishing into a redis channel the information recollected.
 * 
 * @author arcturus@ardeenelinfierno.com
 *
 */
public class RedisMarketBolt extends RedisBolt implements OnDynamicConfigurationListener {
	
	public static final String CHANNEL = "market";
	private final List<String> forbiddenUrls = new LinkedList<String>();

	public RedisMarketBolt() {
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
		URL marketURL = getMarketUrl();
		if(marketURL == null) {
			return null;
		}
		
		synchronized (forbiddenUrls) {
			if(forbiddenUrls.contains(marketURL.toString())) {
				log.debug("Forbidden app: " + marketURL.toString());
				return null;
			}
		}
		
		
		try {
			Document doc = Jsoup.parse(marketURL, 10000);
			String htmlTitle = doc.title();
			String icon = doc.select("div.doc-banner-icon").first().select("img").first().attr("src");
			String ratingValue = doc.select("div.ratings").first().attr("title");
			Elements ratingElements = doc.select("td.doc-details-ratings-price").first().select("div[title]");
			String ratingCount = ratingElements.size() >= 2 ? ratingElements.get(1).attr("title") : "";
			String description = doc.select("div#doc-original-text").first().html();
			String title = doc.select("h1.doc-banner-title").first().html();
			
			JSONObject json = new JSONObject();
			
			json.put("title", title);
			json.put("icon", icon);
			json.put("ratingValue", ratingValue);
			json.put("ratingCount", ratingCount);
			json.put("description", description);
			json.put("url", marketURL.toString());
			
			publish(json.toJSONString());
			
		} catch (IOException e) {
			return null;
		} catch (NullPointerException e) {
			//If there is any problem parsing the html, just give up.
			return null;
		}
		return null;
	}
	
	private URL getMarketUrl() {
		String jsonSource = (String)currentTuple.getValue(0);
		
		JSONParser parser = new JSONParser();
		JSONObject json;
		try {
			json = (JSONObject) parser.parse(jsonSource);
		} catch (ParseException e) {
			return null;
		}
		
		if(json == null) {
			return null;
		}
		
		String url = (String)json.get("link");
		if(url == null) {
			return null;
		}
				
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public void onConfigurationChange(String conf) {
		Utils.StringToList(conf, forbiddenUrls);		
	}

}
