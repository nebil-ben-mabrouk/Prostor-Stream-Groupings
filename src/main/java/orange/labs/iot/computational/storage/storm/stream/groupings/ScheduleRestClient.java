package orange.labs.iot.computational.storage.storm.stream.groupings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.shade.org.json.simple.JSONObject;
import org.apache.storm.shade.org.json.simple.parser.JSONParser;
import org.apache.storm.shade.org.json.simple.parser.ParseException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import orange.labs.iot.computational.storage.util.PropertyManager;

public class ScheduleRestClient {

	private static Client scheduleRestClient = Client.create();

	public static Map<Integer,String> getSchedule(String topology, String component) throws IOException, ParseException {
		
		Map<Integer,String> assignments = new HashMap<Integer,String>();
		if (!(topology.equals("")|| null==topology || component.equals("")|| null==component)){
			String jsonString = connectToScheduleResource(topology, component);
			
			if (!jsonString.equals("")){
				JSONObject assignmentList = (JSONObject) new JSONParser().parse(jsonString);
				if (!assignmentList.keySet().isEmpty())
				for (Object executor: assignmentList.keySet()){
					assignments.put(Integer.valueOf((String)executor), (String)assignmentList.get(executor));
				}
			}
		}
		return assignments;
	}

	private static String connectToScheduleResource(String topologyId, String componentId) throws IOException {
		String result = "";
		
		String resourceUri = PropertyManager.getPropertyValue("schedule.resource.uri")+topologyId+"/"+componentId;
		
		WebResource scheduleResource = scheduleRestClient.resource(resourceUri);
		ClientResponse response = scheduleResource.accept("application/json").get(ClientResponse.class);

		if (response.getStatus() == 200)
			result = response.getEntity(String.class);
		
		return result;
	}
}
