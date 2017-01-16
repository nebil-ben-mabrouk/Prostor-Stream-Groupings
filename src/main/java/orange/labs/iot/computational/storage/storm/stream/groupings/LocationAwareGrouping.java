package orange.labs.iot.computational.storage.storm.stream.groupings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.storm.shade.com.google.common.collect.HashMultimap;

import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.grouping.CustomStreamGrouping;
import org.apache.storm.task.WorkerTopologyContext;
import org.apache.storm.utils.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationAwareGrouping implements CustomStreamGrouping, Serializable {

	private static final long serialVersionUID = 8386933687353152594L;

	private Map<String,String> groupings = new HashMap<String,String>();
	private Map<Integer,List<Integer>> selectedTasks = new HashMap<Integer,List<Integer>>();
	
	private static final Logger LOG = LoggerFactory.getLogger(LocalGrouping.class);

	public LocationAwareGrouping(String[] groupingLinks){		
		for (String groupingLink : groupingLinks) {
			String[] link = groupingLink.split("->");
			if (link.length == 2) groupings.put(link[0], link[1]);
		}
		LOG.debug("LocationAwareGrouping: " + groupings.toString());
	}

	@Override
	public void prepare(WorkerTopologyContext context, GlobalStreamId stream, List<Integer> targetTasks) {
		String sourceComponentId = "";		
		String sourceLocation = "";
		String targetLocation = "";
		
		try {
			
		//initialize source and target components
		if (stream.is_set_componentId()) sourceComponentId = stream.get_componentId();
		String targetComponentId = context.getComponentId(targetTasks.get(0)); 

		LOG.debug("LocationAwareGrouping: sourceComponentId: " + sourceComponentId);
		LOG.debug("LocationAwareGrouping: targetComponentId: " + targetComponentId);
		
		Map<Integer,String> sourceAssignments = new HashMap<Integer,String>();
		Map<Integer,String> targetAssignments = new HashMap<Integer,String>();
		HashMultimap<String,Integer> invertedTargetAssignments =  HashMultimap.create();
		
		while(selectedTasks.isEmpty()){
			
			sourceAssignments = ScheduleRestClient.getSchedule(context.getStormId(), sourceComponentId);
			targetAssignments = ScheduleRestClient.getSchedule(context.getStormId(), targetComponentId);
	
			if (!(sourceAssignments.isEmpty() || targetAssignments.isEmpty())){				
			
				for (Integer executor:targetAssignments.keySet()){
					invertedTargetAssignments.put(targetAssignments.get(executor),executor);
				}
		
				LOG.debug("LocationAwareGrouping: Source assignments: " + sourceAssignments.toString());
				LOG.debug("LocationAwareGrouping: Target assignments: " + invertedTargetAssignments.toString());
		
				//process the source component assignments
				for(Integer task: context.getComponentTasks(sourceComponentId)){
					for (Integer executor : sourceAssignments.keySet()) {
						if (executor == task){
							sourceLocation = sourceAssignments.get(executor);				
							targetLocation = groupings.get(sourceLocation);		
							if (targetLocation!=null){
								List<Integer> chosenTask = new ArrayList<Integer>();
								chosenTask.addAll(invertedTargetAssignments.get(targetLocation));
								selectedTasks.put(task, chosenTask);
							}				
						}
					}
				}
			}
			
			Utils.sleep(1000);
		}
		
		LOG.debug("LocationAwareGrouping: Selected tasks: " + selectedTasks.toString());

		} catch (Exception e) {
			LOG.debug("LocationAwareGrouping: Exception occurred: " + e.getMessage());
		}
	}

	@Override
	public List<Integer> chooseTasks(int taskId, List<Object> values) {			
		return selectedTasks.get(taskId);			
	}
}
