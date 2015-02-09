package my.JobShop.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.JobShop.obj.JobShopNode;
import my.JobShop.obj.RouteTime;
import my.JobShop.obj.SingleMachineNode;

/***
 * Class Name: Log
 * Description: Use to log the process of algorithm
 */
public class Log {
	private String filePath;
	
	public Log(String filePath){
		this.filePath = filePath;
	}
	
	public void logLowerBound(List<Integer> order, int lowerBound) {
		FileProcess.writeStringToFile(filePath, "Node " + toStringWithOrder(order) + " LB :" + Integer.toString(lowerBound));
	}
	
	public static String toStringWithOrder(List<Integer> order) {
		String output = "(";
		for (int i : order)
		{
			output += (i > 0 ? Integer.toString(i) : "0") + ",";
		}
		return output.substring(0, output.length() - 1) + ")";
	}
	
	public void logSingleMachine(List<SingleMachineNode> singleMachineNodeList) {
		StringBuffer msg = new StringBuffer();
		for (SingleMachineNode singleMachineNode : singleMachineNodeList) {
			msg.append(singleMachineNode.toString());
		}
		FileProcess.writeStringToFile(filePath, msg.toString());
	}
	
	public void logSingleMachineWithTime(List<SingleMachineNode> singleMachineNodes) {
		List<SingleMachineNode> tempList = new ArrayList<SingleMachineNode>(singleMachineNodes);
		StringBuffer sb = new StringBuffer();
		sb.append("Best Solution Detail: [");
		while (tempList.size() > 0) {
			int index = 0;
			int minEndTime = 999;
			for (int i = 0; i < tempList.size(); i++) {
				SingleMachineNode node = tempList.get(i);
				if (node.getEndTime() < minEndTime) {
					minEndTime = node.getEndTime();
					index = i;
				}
			}
			SingleMachineNode selectedNode = tempList.get(index);
			int jobId = selectedNode.getJobId();
			int startTime = selectedNode.getEndTime() - selectedNode.getProcessTime();
			int endTime = selectedNode.getEndTime();
			sb.append(String.format("%d(%d,%d),", jobId, startTime, endTime));;
			tempList.remove(index);
		}
		FileProcess.writeStringToFile(filePath, sb.substring(0, sb.length() - 1) + "]");
	}
	
	public void logSingleMachineWithTimeWithEdd(List<SingleMachineNode> singleMachineNodes, List<Integer> orders) {
		Map<Integer, SingleMachineNode> nodeMap = new HashMap<Integer, SingleMachineNode>();
		for (SingleMachineNode node : singleMachineNodes) {
			nodeMap.put(node.getJobId(), node);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("Best Solution Detail: [");
		for (Integer jobId : orders) {
			SingleMachineNode selectedNode = nodeMap.get(jobId);
			List<RouteTime> routeTimes = selectedNode.getRouteTimeList();
			RouteTime routeTime = routeTimes.get(0);
			int startTime = routeTime.getStartTime();
			int endTime = routeTime.getEndTime();
			sb.append(String.format("%d(%d,%d),", jobId, startTime, endTime));;
			routeTimes.remove(0);
		}
		FileProcess.writeStringToFile(filePath, sb.substring(0, sb.length() - 1) + "]");
	}
	
	public void logDivideLine() {
		String msg = "------------------------------";
		FileProcess.writeStringToFile(filePath, msg);
	}
	
	public void logJobShop(List<JobShopNode> jobShopNodeList) {
		String msg = String.format("=== %s ===", jobShopNodeList);
		FileProcess.writeStringToFile(filePath, msg);
	}
	
	public void logJobShop(int maxspan) {
		String msg = String.format("maxspan : %d", maxspan);
		FileProcess.writeStringToFile(filePath, msg);
	}
	
	public void logJobShop(List<JobShopNode> jobShopNodeList, int maxspan) {
		String msg = jobShopNodeList.toString() + ", maxspan: " + Integer.toString(maxspan);
		FileProcess.writeStringToFile(filePath, msg);
	}
	
	public void logJobShopWithMachine(List<JobShopNode> jobShopNodeList, int maxspan) {
		StringBuffer msg = new StringBuffer();
		msg.append("---- maxspan:").append(maxspan).append("-----");
		int machineId = 0;
		for (JobShopNode jobShopNode : jobShopNodeList) {
			if (jobShopNode.getMachineId() != machineId) {
				machineId = jobShopNode.getMachineId();
				msg.append("\r\n").append(machineId).append(" ");
			}
			msg.append(jobShopNode.getJobId()).append(" ");
		}
		msg.append("\r\n").append("--------").append("\r\n");
		FileProcess.writeStringToFile(filePath, msg.toString());
	}
	
	public void logText(String text) {
		FileProcess.writeStringToFile(filePath, text);
	}
	
	public  void clearLog() {
		FileProcess.clearFileContent(filePath);
	}
}