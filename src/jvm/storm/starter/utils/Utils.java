package storm.starter.utils;

import java.util.Arrays;
import java.util.List;

public class Utils {

	public static final void StringToList(String message, List<String> list) {
		if(message == null) {
			return;
		}
		
		synchronized (list) {
			list.clear();
			String[] domains = message.split(",");
			if(domains != null) {
				list.addAll(Arrays.asList(domains));
			}
		}
	}
}
