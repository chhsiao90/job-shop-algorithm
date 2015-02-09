package my.JobShop.calc;

import java.util.ArrayList;
import java.util.List;

import my.JobShop.obj.SingleMachineNode;

public class SingleMachineCalculationNew extends SingleMachineCalculation {

	private int nodeQty;

	public SingleMachineCalculationNew(List<SingleMachineNode> jobList) {
		super(jobList);
		nodeQty = 0;
	}

	@Override
	public int getLowerBoundRecur(List<Integer> order) {
		return getLowerBoundRecur(order, 0, 0, 999);
	}

	// 樹狀結構遞迴計算LB
	public int getLowerBoundRecur(List<Integer> order, int nowTime, int level,
			int lowerBound) {
		int[] checkNumList = new int[jobNum];
		int nextStartTime = 0;
		List<Integer> nextOrder = null;
		SingleMachineCalculationNew calculationBest = null;
		int lowerBoundNow = 999;
		// Set the check number list
		for (int jobId = 0; jobId < jobNum; jobId++) {
			if (order.contains(jobId + 1))
				checkNumList[jobId] = 999;
			else {
				SingleMachineNode singleMachineNode = jobList.get(jobId);
				checkNumList[jobId] = Math.max(nowTime,
						singleMachineNode.getReleaseTime())
						+ singleMachineNode.getProcessTime();
			}
		}

		// Start calculate the lower bound
		for (int jobId = 0; jobId < jobNum; jobId++) {
			// if the job is complete or this situation can't be the best
			// solution
			if (order.contains(jobId + 1)
					|| jobList.get(jobId).getReleaseTime() >= getMinCheckNum(
							checkNumList, jobId))
				continue;
			int startTime = nowTime;
			++nodeQty;

			// Calculate the time
			List<SingleMachineNode> jobListTemp = copyListJob();
			SingleMachineNode nodeNow = jobListTemp.get(jobId);
			if (nodeNow.getReleaseTime() > startTime)
				startTime = nodeNow.getReleaseTime();
			startTime += nodeNow.getProcessTime();
			nodeNow.setActualTime(nodeNow.getProcessTime());
			nodeNow.setEndTime(startTime);

			SingleMachineCalculationNew calculation = new SingleMachineCalculationNew(
					jobListTemp);
			List<Integer> tempOrder = new ArrayList<Integer>(order);
			tempOrder.set(level, jobId + 1);

			if (level == jobNum - 2)
				setEndOrder(tempOrder);
			int lowerBoundTemp;

			SingleMachineCalculationNew calculationWithEdd = new SingleMachineCalculationNew(
					calculation.copyListJob());
			calculationWithEdd.setLog(log);
			lowerBoundTemp = calculationWithEdd.getLowerBoundByEdd(startTime);
			// log.logLowerBound(tempOrder, lowerBoundTemp);
			// If the situation hit the end and is best solution
			if (level == jobNum - 2 && lowerBoundTemp == lowerBound) {
				lowerBoundNow = lowerBoundTemp;
				nextOrder = tempOrder;
				solutionJobList = calculationWithEdd.getJobList();
			} else if (lowerBoundTemp < lowerBoundNow) {
				calculationBest = calculation;
				lowerBoundNow = lowerBoundTemp;
				nextStartTime = startTime;
				nextOrder = tempOrder;
				solutionJobList = level == jobNum - 2 ? calculationWithEdd
						.getJobList() : calculation.getJobList();
			}
		}

		// If the node didn't hit the end
		if (level < jobNum - 2) {
			calculationBest.setLog(log);
			lowerBoundNow = calculationBest.getLowerBoundRecur(nextOrder,
					nextStartTime, level + 1, lowerBoundNow);
			nodeQty += calculationBest.getNodeQty();
			bestOrder = calculationBest.getBestOrder();
			solutionJobList = calculationBest.getSolutionJobList();
		} else {
			bestOrder = nextOrder;
		}

		return lowerBoundNow;
	}

	public void setEndOrder(List<Integer> order) {
		for (int i = 1; i <= jobNum; i++) {
			if (!order.contains(i)) {
				order.set(order.size() - 1, i);
				break;
			}
		}
	}

	public int getMinCheckNum(int[] checkNumList, int jobId) {
		int min = 999;
		for (int checkNum : checkNumList) {
			if (!(checkNum == jobId) && checkNum < min)
				min = checkNum;
		}
		return min;
	}

	public int getNodeQty() {
		return nodeQty;
	}

	public int getEliminateNodeQty() {
		return getSumNodeQty() - nodeQty;
	}
	
	public int getSumNodeQty() {
		int sum = 1;
		int now = 1;
		for (int i = 0; i < jobNum - 1; i++) {
			now = (jobNum - i) * now;
			sum += now;
		}
		return sum;
	}

}
