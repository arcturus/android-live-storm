package storm.starter.bolt;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.json.simple.JSONObject;

import com.gravity.goose.Article;
import com.gravity.goose.Configuration;
import com.gravity.goose.Goose;
import com.gravity.goose.network.NotHtmlException;

import twitter4j.Status;
import twitter4j.URLEntity;

public class RedisGooseExtractor extends RedisBolt {
	
	public static final String CHANNEL = "articles";
	private Goose goose;
	
	public RedisGooseExtractor() {
		super(CHANNEL);
	}
	
	@Override
	protected void setupNonSerializableAttributes() {
		super.setupNonSerializableAttributes();
		
		Configuration config = new Configuration();
		config.enableImageFetching_$eq(false); //No images right now, it requires imagemagik installed in a specific path
		goose = new Goose(config);
	}

	@Override
	public List<Object> filter(Status status) {
		URLEntity urls[] = status.getURLEntities();
		if(urls == null || urls.length == 0) {
			return null;
		}
		
		if(urls[0].getExpandedURL() == null) {
			return null;
		}
		
		String articleUrl = urls[0].getExpandedURL().toString();
		Article article = null;
		try {
			article = goose.extractContent(articleUrl);
		} catch(Exception e) {
			System.out.println(articleUrl);
			System.out.println(e.getMessage());
			return null;
		}
		
		if(article == null ||
				article.title() == null ||
				article.title().length() == 0 ||
				article.cleanedArticleText() == null ||
				article.cleanedArticleText().length() == 0) {
			return null;
		}
		
		JSONObject json = new JSONObject();
		json.put("url", articleUrl);
		json.put("title", article.title());
		json.put("text", article.cleanedArticleText());
		json.put("description", article.metaDescription());
		if(article.topImage() != null && article.topImage().imageSrc().length() != 0) {
			json.put("image", article.topImage().imageSrc());
		}
		
		publish(json.toJSONString());
		
		return null;
	}

}
