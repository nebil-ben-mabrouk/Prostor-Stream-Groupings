package orange.labs.iot.computational.storage.storm.stream.groupings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.grouping.CustomStreamGrouping;
import org.apache.storm.task.WorkerTopologyContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalGrouping implements CustomStreamGrouping, Serializable {

	private static final long serialVersionUID = -3984783005083846162L;
	
	private List<Integer> selectedTasks = new ArrayList<Integer>();
	
	private static final Logger LOG = LoggerFactory.getLogger(LocalGrouping.class);
	
	@Override
	public void prepare(WorkerTopologyContext context, GlobalStreamId stream, List<Integer> targetTasks) {
		String sourceComponentId = "";
		if (stream.is_set_componentId()) sourceComponentId = stream.get_componentId();
		
		List<Integer> concurrentTasks = context.getThisWorkerTasks();
		
		for (int task:targetTasks){
			if (concurrentTasks.contains(task)) {
				selectedTasks.add(task);
				LOG.debug("LocalGrouping: " + sourceComponentId + "==>" + selectedTasks.toString());
			}
		}		
	}

	@Override
	public List<Integer> chooseTasks(int taskId, List<Object> values) {
		return selectedTasks;
	}

}
