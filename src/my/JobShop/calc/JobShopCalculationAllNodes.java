package my.JobShop.calc;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import my.JobShop.obj.JobShopData;
import my.JobShop.obj.JobShopNode;

public class JobShopCalculationAllNodes extends JobShopCalculation {

	private BigInteger allNodeQty = new BigInteger("0");

	public JobShopCalculationAllNodes(List<JobShopData> jobShopDataList)
			throws Exception {
		super(jobShopDataList);
	}

	// Get the best of maxspan
	@Override
	public int getBestSolution(JobShopNode parentJobShopNode, int maxspan,
			Map<Integer, List<JobShopNode>> solutionMap) {
		// Increase the node quantity
		allNodeQty = allNodeQty.add(new BigInteger("1"));
		// Step 1 & Step 2
		int[] selectedMachine = parentJobShopNode.getMachineSelection();
		if (selectedMachine[0] == 0) {
			// Increase the leaf quantity
			++leafQty;
			return maxspan;
		}
		// Step 3
		List<JobShopNode> jobShopNodeWithMachine = parentJobShopNode
				.getJobShopNodeListWithMachine(selectedMachine[0]);
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

				// 取得下一層的maxspan
				getBestSolution(branchJobShopNode, maxspan, solutionMap);
			}
		}
		return maxspan;
	}

	public BigInteger getAllNodeQty() {
		return allNodeQty;
	}

}
