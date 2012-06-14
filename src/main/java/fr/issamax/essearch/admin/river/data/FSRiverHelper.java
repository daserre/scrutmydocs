package fr.issamax.essearch.admin.river.data;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.support.XContentMapValues;

public class FSRiverHelper {
	
	/**
	 * Build a river definition for FS
	 * @param index Index where to send documents
	 * @param type Type where to store documents
	 * @param fsriver The river definition
	 * @return An ES xcontent
	 */
	public static XContentBuilder toXContent(String index, String type, FSRiver fsriver) {
		XContentBuilder xb = null;
		try {
			xb = jsonBuilder()
					.startObject()
						.field("type", "fs")
						.startObject("fs")
							.field("name", fsriver.getName())
							.field("url", fsriver.getUrl())
							.field("update_rate", fsriver.getUpdateRate() * 1000)
						.endObject()
						.startObject("index")
							.field("index", index)
							.field("type", type)
						.endObject()
					.endObject();
		} catch (IOException e) {
			// TODO Log when error
		}		
		return xb;
	}
	
	
	/**
	 * Build a FS river from a JSON definiton content such as :<pre>
{
  "type" : "fs",
  "fs" : {
	  "update_rate" : 30000,
	  "name" : "tmp",
	  "url" : "/tmp_es"
  },
  "index" : {
	  "index" : "docs",
	  "type" : "doc"
  }
}
</pre>
	 * @param index Index where to send documents
	 * @param type Type where to store documents
	 * @param fsriver The river definition
	 * @return An ES xcontent
	 */
	public static FSRiver toFSRiver(Map<String, Object> content) {
		FSRiver fsriver = new FSRiver();
		try {
			// First we check that it's a fs type
			if (!content.containsKey("type")) 
				throw new RuntimeException("Your River object should be a river and contain \"type\":\"rivertype\"");
			if (!(XContentMapValues.nodeStringValue(content.get("type"), "")).equalsIgnoreCase("fs")) 
				throw new RuntimeException("Your FSRiver object should be a river and contain \"type\":\"fs\"");
			fsriver.setType("fs");
			
			// Then we dig into fs.fs[0]
			if (!content.containsKey("fs")) 
				throw new RuntimeException("A FSRiver must contain \"fs\":{...}");

			fsriver.setName(getSingleStringValue("fs.name", content));
			fsriver.setUrl(getSingleStringValue("fs.url", content));
			fsriver.setUpdateRate(getSingleLongValue("fs.update_rate", content) / 1000);
			
		} catch (Exception e) {
			// TODO Log when error
		}		
		return fsriver;
	}
	
	private static String getSingleStringValue(String path, Map<String, Object> content) {
		List<Object> obj = XContentMapValues.extractRawValues(path, content);
		return ((String) obj.get(0));
	}
	
	private static Long getSingleLongValue(String path, Map<String, Object> content) {
		List<Object> obj = XContentMapValues.extractRawValues(path, content);
		return ((Integer) obj.get(0)).longValue();
	}
}