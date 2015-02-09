package my.JobShop.calc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import my.JobShop.obj.JobShopData;
import my.JobShop.obj.JobShopNode;
import my.JobShop.obj.SingleMachineNode;
import my.JobShop.util.Log;

/***
 * Class: JobShopCalculation Description: Calculate the maxspan of jobshop
 * problem
 */
public class JobShopCalculation {
	protected List<JobShopData> jobShopDataList;
	protected JobShopNode startNode = new JobShopNode(0, 0, 0);
	protected int jobNum;
	protected int machineNum;
	protected Log log;
	protected int nodeQty;
	protected int leafQty;
	protected int bestMaxspan;
	protected long singleMachineProceessTime = 0;
	protected static int MAX = 999;

	// Constructor
	public JobShopCalculation(List<JobShopData> jobShopDataList)
			throws Exception {
		this.jobShopDataList = jobShopDataList;
		nodeQty = 0;
		bestMaxspan = 999;
		setJobNum();
		setMachineNum();
		setJobShopNodeList();
	}

	// Initialize the max number of job
	private void setJobNum() {
		int maxJob = 0;
		for (JobShopData jobShopData : jobShopDataList) {
			maxJob = jobShopData.getToJobId() > maxJob ? jobShopData
					.getToJobId() : maxJob;
		}
		jobNum = maxJob;
	}

	// Initialize the max number of machine
	private void setMachineNum() {
		int maxMachine = 0;
		for (JobShopData jobShopData : jobShopDataList) {
			maxMachine = jobShopData.getToMachineId() > maxMachine ? jobShopData
					.getToMachineId() : maxMachine;
		}
		machineNum = maxMachine;
	}

	// Transform the data from file to the data of jobshop
	protected void setJobShopNodeList() throws Exception {
		List<JobShopNode> jobShopNodeList = new ArrayList<JobShopNode>();
		jobShopNodeList.add(startNode);
		JobShopNode jobShopNode = new JobShopNode(machineNum, jobNum, 0);
		jobShopNodeList.add(jobShopNode);
		for (JobShopData jobShopData : jobShopDataList) {
			jobShopNode = jobShopData.getFromJobShopNode();
			if (getJobShopNodeFromList(jobShopNode, jobShopNodeList) == null)
				jobShopNodeList.add(jobShopNode);
		}
		for (JobShopData jobShopData : jobShopDataList) {
			JobShopNode fromJobShopNode = getJobShopNodeFromList(
					jobShopData.getFromJobShopNode(), jobShopNodeList);
			JobShopNode toJobShopNode = getJobShopNodeFromList(
					jobShopData.getToJobShopNode(), jobShopNodeList);
			if (fromJobShopNode == null || toJobShopNode == null)
				throw new Exception("Input file format was wrong!");
			fromJobShopNode.addChildNode(toJobShopNode);
		}
	}

	// Get the best of maxspan
	public int getBestSolution(JobShopNode parentJobShopNode, int maxspan,
			Map<Integer, List<JobShopNode>> solutionMap) {
		// Increase the node quantity
		++nodeQty;
		// Step 1 & Step 2
		int[] selectedMachine = parentJobShopNode.getMachineSelection();
		if (selectedMachine[0] == 0) {
			// Increase the leaf quantity
			++leafQty;
			if (!solutionMap.containsKey(maxspan))
				solutionMap.put(maxspan, new ArrayList<JobShopNode>());
			solutionMap.get(maxspan).add(parentJobShopNode);
			log.logJobShopWithMachine(
					parentJobShopNode.getCompleteListWithMachine(), maxspan);
			bestMaxspan = bestMaxspan > maxspan ? maxspan : bestMaxspan;
			return maxspan;
		}
		// Step 3
		List<JobShopNode> jobShopNodeWithMachine = parentJobShopNode
				.getJobShopNodeListWithMachine(selectedMachine[0]);
		int compareMaxspan = MAX;
		for (JobShopNode jobShopNode : jobShopNodeWithMachine) {
			if (jobShopNode.getReleaseTime() <= selectedMachine[1]
					&& !jobShopNode.isComplete()) {
				JobShopNode branchJobShopNode = parentJobShopNode.copy();
				// Add disjunctive arc
				List<JobShopNode> branchJobShopNodeWithMachine = branchJobShopNode
						.getJobShopNodeListWithMachine(selectedMachine[0]);
				jobShopNode = branchJobShopNode.findJobShopNode(jobShopNode);
				jobShopNode.setDisjunctiveArc(branchJobShopNodeWithMachine);
				// Add the node to complete list
				int complete = branchJobShopNode.getComplete() + 1;
				branchJobShopNode.setComplete(complete);
				jobShopNode.setComplete(complete);
				// Log
				log.logJobShop(branchJobShopNode.getCompleteList());
				log.logText(String.format("OMEGA: Machine %d",
						selectedMachine[0]));
				// Single Machine Problem去取得最大的maxspan
				if (maxspan == 0)
					maxspan = branchJobShopNode.getMaxLength();
				int maxLowerBound = 0;
				long startTime = System.currentTimeMillis();
				for (int machineId = 1; machineId < machineNum; machineId++) {
					// Set order list and node list
					List<SingleMachineNode> singleMachineNodeList = new ArrayList<SingleMachineNode>();
					List<Integer> orderList = new ArrayList<Integer>();
					branchJobShopNodeWithMachine = branchJobShopNode
							.getJobShopNodeListWithMachine(machineId);
					if (branchJobShopNodeWithMachine.size() == 0)
						continue;
					for (JobShopNode singleMachineJobShopNode : branchJobShopNodeWithMachine) {
						int jobId = singleMachineJobShopNode.getJobId();
						int processTime = singleMachineJobShopNode
								.getProcessTime();
						int releaseTime = singleMachineJobShopNode
								.getReleaseTime();
						int dueDate = maxspan
								- singleMachineJobShopNode.getMaxLength()
								+ processTime;
						SingleMachineNode singleMachineNode = new SingleMachineNode(
								jobId, processTime, releaseTime, dueDate);
						singleMachineNodeList.add(singleMachineNode);
						orderList.add(new Integer(0));
					}
					// Calculate the single machine problem
					SingleMachineCalculationNew singleMachineCalculation = new SingleMachineCalculationNew(
							singleMachineNodeList);
					singleMachineCalculation.setLog(log);
					int lowerBound = singleMachineCalculation
							.getLowerBoundRecur(orderList);
					List<Integer> bestOrder = singleMachineCalculation
							.getBestOrder();
					setOrder(bestOrder, singleMachineNodeList);
					log.logText(String.format("-Machine : %d", machineId));
					log.logSingleMachine(singleMachineCalculation.getJobList());
					log.logText(String
							.format("Best Solution: %s, lowerbound: %d\r\n Eliminate Nodes : %d\r\n All Nodes : %d",
									Log.toStringWithOrder(bestOrder),
									lowerBound, singleMachineCalculation
											.getEliminateNodeQty(),
									singleMachineCalculation.getSumNodeQty()));
					log.logSingleMachineWithTime(singleMachineCalculation
							.getSolutionJobList());
					maxLowerBound = lowerBound > maxLowerBound ? lowerBound
							: maxLowerBound;
				}
				singleMachineProceessTime += System.currentTimeMillis()
						- startTime;
				if (maxLowerBound == 999) {
					log.logJobShop(999);
					continue;
				}
				log.logJobShop(maxspan + maxLowerBound);
				// 取得下一層的maxspan
				if (bestMaxspan >= maxspan + maxLowerBound) {
					int tempMaxspan = getBestSolution(branchJobShopNode,
							maxspan + maxLowerBound, solutionMap);
					compareMaxspan = (compareMaxspan == 0 || compareMaxspan > tempMaxspan) ? tempMaxspan
							: compareMaxspan;
				}
			}
		}
		return compareMaxspan;
	}

	// 檢查jobshop的節點是否已經存在在list裡
	protected JobShopNode getJobShopNodeFromList(JobShopNode jobShopNode,
			List<JobShopNode> jobShopNodeList) {
		for (JobShopNode node : jobShopNodeList) {
			if (jobShopNode.isEqual(node))
				return node;
		}
		return null;
	}

	public void logResult(Map<Integer, List<JobShopNode>> solutionMap,
			int maxspan, String allNodesQty) {
		for (JobShopNode jobShopNode : solutionMap.get(maxspan)) {
			log.logJobShop(jobShopNode.getCompleteList());
			log.logJobShopWithMachine(jobShopNode.getCompleteListWithMachine(),
					maxspan);
		}
		log.logText(String.format("All Nodes : %s", allNodesQty));
		log.logText(String.format("Optimal Nodes : %d", nodeQty));
		log.logText(String.format("Optimal Leaves : %d",
				solutionMap.get(maxspan).size()));
		log.logText(String.format("Solution Leaves : %d", leafQty));
		// log.logText(String.format("All Nodes : %d", nodeQty));
	}

	protected void setOrder(List<Integer> orders,
			List<SingleMachineNode> jobList) {
		for (int i = 0; i < orders.size(); i++) {
			orders.set(i, jobList.get(orders.get(i) - 1).getJobId());
		}
	}

	/**
	 * getter and setter
	 */
	public JobShopNode getStartNode() {
		return startNode;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public int getNodeQty() {
		return nodeQty;
	}

	public void setNodeQty(int nodeQty) {
		this.nodeQty = nodeQty;
	}

	public int getLeafQty() {
		return leafQty;
	}

	public void setLeafQty(int leafQty) {
		this.leafQty = leafQty;
	}

	public long getSingleMachineProceessTime() {
		return singleMachineProceessTime;
	}
}