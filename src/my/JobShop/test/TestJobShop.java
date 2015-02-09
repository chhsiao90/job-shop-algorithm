package my.JobShop.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.JobShop.calc.JobShopCalculation;
import my.JobShop.calc.JobShopCalculationAllNodes;
import my.JobShop.calc.JobShopCalculationEdd;
import my.JobShop.obj.JobShopData;
import my.JobShop.obj.JobShopNode;
import my.JobShop.util.FileProcess;
import my.JobShop.util.Log;
import my.JobShop.util.RandomGenInput;

import org.junit.Test;

public class TestJobShop {

	@Test
	public void testJobShop() {
		int machineQty = 6;
		int jobQty = 6;
		int pathQty = 36;
		RandomGenInput randomGenInput = new RandomGenInput(machineQty, jobQty,
				pathQty);
		randomGenInput.process();
		randomGenInput.writeResultToFile("input.txt");
		try {
			Log log = new Log("logNormal.txt");
			log.clearLog();
			List<JobShopData> jobShopDataList = FileProcess
					.getJobShopDataList("input.txt");
			// Get the all nodes qty
			JobShopCalculationAllNodes jobShopCalculationAllNodes = new JobShopCalculationAllNodes(
					jobShopDataList);
			jobShopCalculationAllNodes.getBestSolution(
					jobShopCalculationAllNodes.getStartNode(), 0, null);
			String allNodesQty = jobShopCalculationAllNodes.getAllNodeQty()
					.toString();
			// Get the solution WithNormal
			JobShopCalculation jobShopCalculation = new JobShopCalculation(
					jobShopDataList);
			jobShopCalculation.setLog(log);
			long time = System.currentTimeMillis();
			Map<Integer, List<JobShopNode>> solutionMap = new HashMap<Integer, List<JobShopNode>>();
			int maxspan = jobShopCalculation.getBestSolution(
					jobShopCalculation.getStartNode(), 0, solutionMap);
			time = System.currentTimeMillis() - time;
			long singleMachineTime = jobShopCalculation
					.getSingleMachineProceessTime();
			jobShopCalculation.logResult(solutionMap, maxspan, allNodesQty);
			log.logText(String.format("All Processing Time: %.3f sec",
					(float) time / 1000f));
			log.logText(String.format(
					"Singe Machine Processing Time: %.3f sec",
					(float) singleMachineTime / 1000f));

			// Get the solution With Edd
			log = new Log("logEdd.txt");
			log.clearLog();
			jobShopCalculation = new JobShopCalculationEdd(jobShopDataList);
			jobShopCalculation.setLog(log);
			time = System.currentTimeMillis();
			solutionMap = new HashMap<Integer, List<JobShopNode>>();
			maxspan = jobShopCalculation.getBestSolution(
					jobShopCalculation.getStartNode(), 0, solutionMap);
			time = System.currentTimeMillis() - time;
			jobShopCalculation.logResult(solutionMap, maxspan, allNodesQty);
			singleMachineTime = jobShopCalculation
					.getSingleMachineProceessTime();
			log.logText(String.format("All Processing Time: %.3f sec",
					(float) time / 1000f));
			log.logText(String.format(
					"Singe Machine Processing Time: %.3f sec",
					(float) singleMachineTime / 1000f));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testJobShopWithEdd() {
		try {
			Log log = new Log("logEdd.txt");
			log.clearLog();
			List<JobShopData> jobShopDataList = FileProcess
					.getJobShopDataList("input.txt");
			// Get the all nodes qty
			JobShopCalculationAllNodes jobShopCalculationAllNodes = new JobShopCalculationAllNodes(
					jobShopDataList);
			jobShopCalculationAllNodes.getBestSolution(
					jobShopCalculationAllNodes.getStartNode(), 0, null);
			String allNodesQty = jobShopCalculationAllNodes.getAllNodeQty()
					.toString();
			// Get the solution
			JobShopCalculationEdd jobShopCalculation = new JobShopCalculationEdd(
					jobShopDataList);
			jobShopCalculation.setLog(log);
			long time = System.currentTimeMillis();
			Map<Integer, List<JobShopNode>> solutionMap = new HashMap<Integer, List<JobShopNode>>();
			int maxspan = jobShopCalculation.getBestSolution(
					jobShopCalculation.getStartNode(), 0, solutionMap);
			time = System.currentTimeMillis() - time;
			jobShopCalculation.logResult(solutionMap, maxspan, allNodesQty);
			long singleMachineTime = jobShopCalculation
					.getSingleMachineProceessTime();
			System.out.println(maxspan);
			log.logText(String.format("All Processing Time: %f sec",
					(float) time / 1000f));
			log.logText(String.format("Singe Machine Processing Time: %f sec",
					(float) singleMachineTime / 1000f));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
