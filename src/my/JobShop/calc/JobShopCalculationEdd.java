package my.JobShop.calc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import my.JobShop.obj.JobShopData;
import my.JobShop.obj.JobShopNode;
import my.JobShop.obj.SingleMachineNode;
import my.JobShop.util.Log;

public class JobShopCalculationEdd extends JobShopCalculation {

	public JobShopCalculationEdd(List<JobShopData> jobShopDataList)
			throws Exception {
		super(jobShopDataList);
	}

	// Get the best of maxspan
	@Override
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
							.getLowerBoundByEdd(0);
					List<Integer> bestOrder = singleMachineCalculation
							.getBestOrder();
					log.logText(String.format("-Machine : %d", machineId));
					log.logSingleMachine(singleMachineCalculation.getJobList());
					log.logText(String.format(
							"Best Solution: %s, lowerbound: %d",
							Log.toStringWithOrder(bestOrder), lowerBound));
					log.logSingleMachineWithTimeWithEdd(
							singleMachineCalculation.getSolutionJobList(),
							bestOrder);
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

}
