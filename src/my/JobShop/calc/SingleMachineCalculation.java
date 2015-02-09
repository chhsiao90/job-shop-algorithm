package my.JobShop.calc;

import java.util.ArrayList;
import java.util.List;

import my.JobShop.obj.RouteTime;
import my.JobShop.obj.SingleMachineNode;
import my.JobShop.util.Log;

/***
 * Class: SingleMachineCalculation
 * Description: Save the list of single machine node,
 *              And Calculate the lower bound of single machine problem
 *              With recursive function
 */
public class SingleMachineCalculation {

	protected List<SingleMachineNode> jobList;
	protected int jobNum;
	protected Log log;
	protected List<SingleMachineNode> solutionJobList;
	protected List<Integer> bestOrder;
	
	public SingleMachineCalculation(List<SingleMachineNode> jobList) {
		this.jobList = jobList;
		this.jobNum = jobList.size();
	}
	
	//樹狀結構遞迴計算LB
	public int getLowerBoundRecur(List<Integer> order) {
		if (order.size() != jobNum) return 0;
		int lowerBound = 999;
		int nowTime = 0;
		List<SingleMachineNode> jobListTemp = copyListJob();
		for (int num = 0; num < jobNum; num++) 
		{
			//Node展開還有*的 計算EDD並遞迴展開取得最佳LowerBound
			if (order.get(num) == 0)
			{
				List<List<Integer>> nextLayerOrder = getNextLayerOrder(order, num, jobNum);
				//展開下一Level的狀況
				for (List<Integer> orderNew : nextLayerOrder) 
				{
					int lowerBoundNew = getLowerBoundRecur(orderNew);
					if (lowerBound > lowerBoundNew)
						lowerBound = lowerBoundNew;
				}
				//計算該Node的Lower Bound
				if (num != 0)
				{
					SingleMachineCalculation nodeList = new SingleMachineCalculation(jobListTemp);
					int lowerBoundNew = nodeList.getLowerBoundByEdd(nowTime);
					if (lowerBound > lowerBoundNew)
						lowerBound = lowerBoundNew;
				}
				break;
			}
			//計算該job的due date
			else
			{
				//若不可能為最佳解直接回傳LB:999
				if (!isImposibleCondition(jobListTemp, order, nowTime, order.get(num)))
				{
					//計算目前job的due date，並將現在時間更新
					SingleMachineNode nodeNow = jobListTemp.get(order.get(num) - 1);
					if (nodeNow.getReleaseTime() > nowTime) 
						nowTime = nodeNow.getReleaseTime();
					nowTime += nodeNow.getProcessTime();
					nodeNow.setActualTime(nodeNow.getProcessTime());
					nodeNow.setEndTime(nowTime);
					
					//若num == jobNum代表job全部排完 直接計算Lower Bound
					if (num == jobNum - 1)
					{
						SingleMachineCalculation nodeList = new SingleMachineCalculation(jobListTemp);
						lowerBound = nodeList.getLowerBound();
					}
				}
				else
					break;
			}
		}
		log.logLowerBound(order, lowerBound);
		return lowerBound;
	}
	
	//用EDD計算LowerBound
	public int getLowerBoundByEdd(int startTime) {
		SingleMachineNode jobFirstRelease = null;
		SingleMachineNode jobFirstDue = null;
		List<SingleMachineNode> jobListTemp = new ArrayList<SingleMachineNode>(jobList);
		List<Integer> tempOrder = new ArrayList<Integer>();
		while (jobListTemp.size() > 0)
		{
			int i = 0;
			//取得還未完成job中release time、due date最小的
			for (i = 0; i < jobListTemp.size(); i++)
			{
				SingleMachineNode job = jobListTemp.get(i);
				//將已完成的job移除
				if (job.isCompleted())
				{
					jobListTemp.remove(job);
					break;
				}
				if (jobFirstRelease == null)
					jobFirstRelease = job;
				if (jobFirstDue == null)
					jobFirstDue = job;
				if (job.getReleaseTime() < jobFirstRelease.getReleaseTime())
					jobFirstRelease = job;
				if (job.getDueDate() < jobFirstDue.getDueDate())
					jobFirstDue = job;
			}
			if (jobFirstRelease == null || jobFirstDue == null || i < jobListTemp.size())
				continue;
			
			//若due date最小的job可馬上作業則計算due date並移除
			else if (jobFirstDue.getReleaseTime() <= startTime ||jobFirstDue == jobFirstRelease)
			{
				startTime = startTime < jobFirstDue.getReleaseTime() ? jobFirstDue.getReleaseTime() : startTime;
				int processTime = jobFirstDue.getProcessTime() - jobFirstDue.getActualTime();
				RouteTime routeTime = new RouteTime(startTime, startTime + processTime);
				startTime += processTime;
				jobFirstDue.setEndTime(startTime);
				jobFirstDue.setActualTime(jobFirstDue.getProcessTime());
				jobFirstDue.getRouteTimeList().add(routeTime);
				tempOrder.add(jobFirstDue.getJobId());
				jobListTemp.remove(jobFirstDue);
				jobFirstDue = null;
				jobFirstRelease = null;
			}
			//若due date最小的job不行作業 則先排release time最小的作業
			else
			{
				if (jobFirstRelease.getReleaseTime() > startTime) 
					startTime = jobFirstRelease.getReleaseTime();
				int processTime = jobFirstDue.getReleaseTime() - startTime;
				int processTime2 = jobFirstRelease.getProcessTime() - jobFirstRelease.getActualTime();
				if (processTime >= processTime2)
				{
					RouteTime routeTime = new RouteTime(startTime, startTime + processTime2);
					startTime += processTime2;
					jobFirstRelease.getRouteTimeList().add(routeTime);
					jobFirstRelease.setEndTime(startTime);
					jobFirstRelease.setActualTime(jobFirstRelease.getProcessTime());
					tempOrder.add(jobFirstRelease.getJobId());
					jobListTemp.remove(jobFirstRelease);
					jobFirstDue = null;
					jobFirstRelease = null;
				}
				else
				{
					RouteTime routeTime = new RouteTime(startTime, startTime + processTime);
					startTime += processTime;
					jobFirstRelease.getRouteTimeList().add(routeTime);
					jobFirstRelease.setActualTime(jobFirstRelease.getActualTime() + processTime);
					tempOrder.add(jobFirstRelease.getJobId());
				}
			}
		}
		bestOrder = tempOrder;
		solutionJobList = jobList;
		return getLowerBound();
	}
	
	//取得下一層Order List
	protected List<List<Integer>> getNextLayerOrder(List<Integer> order, int startNum, int jobNum) {
		List<List<Integer>> nextLayerOrder = new ArrayList<List<Integer>>();
		for (int num = 1; num <= jobNum; num++)
		{
			if (order.contains(num))
				continue;
			List<Integer> orderNew = new ArrayList<Integer>(order);
			orderNew.set(startNum, num);
			nextLayerOrder.add(orderNew);
		}
		
		return nextLayerOrder;
	}
	
	//不可能為最佳條件
	protected boolean isImposibleCondition(List<SingleMachineNode> jobList, List<Integer> order, int startTime, int startJob) {
		int releaseTime = jobList.get(startJob - 1).getReleaseTime();
		if (releaseTime <= startTime) return false;
		for (int num = 1; num <= jobNum; num++)
		{
			if (num == startJob || jobList.get(num-1).isCompleted()) continue;
			if (releaseTime >= startTime + jobList.get(num - 1).getReleaseTime() + jobList.get(num - 1).getProcessTime())
				return true;
		}
		return false;
	}
	
	//取得LowerBound
	public int getLowerBound() {
		int lowerBound = 0;
		for (SingleMachineNode job : jobList)
		{
			int cost = job.getCost();
			if (lowerBound < cost)
				lowerBound = cost;
		}
		return lowerBound;
	}
	
	//複製List
	protected List<SingleMachineNode> copyListJob() {
		List<SingleMachineNode> jobListTemp = new ArrayList<SingleMachineNode>();
		for (SingleMachineNode job : jobList)
		{
			SingleMachineNode jobNew = job.copyNode();
			jobListTemp.add(jobNew);
		}
		return jobListTemp;
	}
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		for (SingleMachineNode singleMachineNode : jobList) {
			stringBuffer.append(singleMachineNode.toString()).append("\r\n");
		}
		return stringBuffer.toString();
	}

	/**
	 * getter and setter
	 */
	public void setLog(Log log) {
		this.log = log;
	}

	public List<SingleMachineNode> getJobList() {
		return jobList;
	}

	public List<SingleMachineNode> getSolutionJobList() {
		return solutionJobList;
	}

	public List<Integer> getBestOrder() {
		return bestOrder;
	}
}