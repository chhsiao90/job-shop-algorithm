package my.JobShop.obj;

import java.util.ArrayList;
import java.util.List;

/***
 * Class: JobShopNode
 * Description: The node for the algorithm of jobshop problem
 */
public class JobShopNode {
	
	private List<JobShopNode> nextNodeList = new ArrayList<JobShopNode>();
	private List<JobShopNode> preNodeList = new ArrayList<JobShopNode>();
	private int machineId;
	private int jobId;
	private int processTime;
	private int complete;
	
	//Constructor
	public JobShopNode(int machineId, int jobId, int processTime) {
		this.machineId = machineId;
		this.jobId = jobId;
		this.processTime = processTime;
		this.complete = 0;
	}
	//Constructor
	public JobShopNode(JobShopNode jobShopNode) {
		this.jobId = jobShopNode.getJobId();
		this.machineId = jobShopNode.getMachineId();
		this.processTime = jobShopNode.getProcessTime();
		this.complete = jobShopNode.getComplete();
	}
	//增加子結點
	public void addChildNode(JobShopNode childNode) {
		nextNodeList.add(childNode);
		childNode.addParentNode(this);
	}
	//連結母結點
	public void addParentNode(JobShopNode parentNode) {
		preNodeList.add(parentNode);
	}
	//取得release time
	public int getReleaseTime() {
		int maxTime = 0;
		for (JobShopNode parentNode : preNodeList)
		{
			int time = parentNode.getParentLength();
			maxTime = time > maxTime ? time : maxTime;
		}
		return maxTime;
	}
	//取得release time
	public int getParentLength() {
		int maxTime = 0;
		for (JobShopNode parentNode : preNodeList)
		{
			int time = parentNode.getParentLength();
			maxTime = time > maxTime ? time : maxTime;
		}
		return processTime + maxTime;
	}
	//取得maxspan
	public int getMaxLength() {
		int maxTime = 0;
		for (JobShopNode childNode : nextNodeList)
		{
			int time = childNode.getMaxLength();
			maxTime = time > maxTime ? time : maxTime;
		}
		return processTime + maxTime;
	}
	//取得step 1 & step 2的machine id以及min(rij + pij)
	public int[] getMachineSelection() {
		int[] machineSelection = new int[2];
		for (JobShopNode childNode : nextNodeList) {
			childNode = childNode.getNextUncompleteChild();
			if (childNode == null)
				continue;
			int time = childNode.getReleaseTime() + childNode.getProcessTime();
			if (time < machineSelection[1] || machineSelection[1] == 0) {
				machineSelection[0] = childNode.getMachineId();
				machineSelection[1] = time;
			}
		}
		return machineSelection;
	}
	//取得下一個未完成的子結點
	public JobShopNode getNextUncompleteChild() {
		if (complete == 0)
			return this;
		for (JobShopNode childNode : nextNodeList) {
			if (childNode.getJobId() == jobId)
			{
				JobShopNode jobShopNode = childNode.getNextUncompleteChild();
				return jobShopNode;
			}
		}
		return null;
	}
	//取得同machine id的所有結點
	public List<JobShopNode> getJobShopNodeListWithMachine(int machineId) {
		List<JobShopNode> jobShopNodeList = new ArrayList<JobShopNode>();
		for (JobShopNode childNode : nextNodeList) {
			childNode = childNode.getNextChildWithMachine(machineId);
			if (childNode != null)
				jobShopNodeList.add(childNode);
		}
		return jobShopNodeList;
	}
	private JobShopNode getNextChildWithMachine(int machineId) {
		if (this.machineId == machineId)
			return this;
		for (JobShopNode childNode : nextNodeList) {
			if (childNode.getJobId() == jobId)
			{
				return childNode.getNextChildWithMachine(machineId);
			}
		}
		return null;
	}
	//新增disjunctive arc
	public void setDisjunctiveArc(List<JobShopNode> jobShopNodeList) {
		for (JobShopNode jobShopNode : jobShopNodeList) {
			if(jobShopNode == this || preNodeList.contains(jobShopNode) || nextNodeList.contains(jobShopNode))
				continue;
			this.addChildNode(jobShopNode);
		}
	}
	//取得同job id、machine id的節點
	public JobShopNode findJobShopNode(JobShopNode jobShopNode) {
		if (this.isEqual(jobShopNode))
			return this;
		for (JobShopNode childJobShopNode : nextNodeList)
		{
			childJobShopNode = childJobShopNode.findJobShopNode(jobShopNode);
			if (childJobShopNode != null)
				return childJobShopNode;
		}
		return null;
	}
	//複製同樣的tree
	public JobShopNode copy() {
		JobShopNode newJobShopNode = new JobShopNode(this);
		for (JobShopNode childNode : nextNodeList) {
			JobShopNode newChildJobShopNode = new JobShopNode(childNode);
			newJobShopNode.addChildNode(newChildJobShopNode);
		}
		for (JobShopNode childNode : nextNodeList) {
			childNode.copy(newJobShopNode);
		}
		
		return newJobShopNode;
	}
	//複製同樣的tree
	private void copy(JobShopNode parent) {
		JobShopNode newJobShopNode = parent.findJobShopNode(this);
		if (newJobShopNode == null)
			newJobShopNode = newJobShopNode == null ? new JobShopNode(this) : newJobShopNode;
		for (JobShopNode childNode : nextNodeList) {
			JobShopNode newChildJobShopNode = parent.findJobShopNode(childNode);
			newChildJobShopNode = newChildJobShopNode == null ? new JobShopNode(childNode) : newChildJobShopNode;
			newJobShopNode.addChildNode(newChildJobShopNode);
		}
		for (JobShopNode childNode : nextNodeList) {
			if (!isDisjunctive(childNode))
				childNode.copy(parent);
		}
	}
	//取得下一個complete的節點
	public JobShopNode findNextCompleteJobShopNode(int complete) {
		if (this.complete == complete && jobId != 0)
			return this;
		for (JobShopNode childJobShopNode : nextNodeList)
		{
			JobShopNode jobShopNode = childJobShopNode.findNextCompleteJobShopNode(complete);
			if (jobShopNode != null)
				return jobShopNode;
		}
		return null;
	}
	//檢查結點是否完成
	public boolean isComplete() {
		return complete > 0;
	}
	//檢查結點是否是disjunctive arc
	public boolean isDisjunctive(JobShopNode childJobShopNode) {
		return (childJobShopNode.getJobId() != jobId && !childJobShopNode.isEndNode());
	}
	@Override
	public String toString() {
		return "(" + machineId + "," + jobId + ")";
	}
	//把樹狀結構print出來
	public void printAll() {
		System.out.print("\n" + toString() + " Child:" );
		for (JobShopNode childNode : nextNodeList)
		{
			System.out.print(childNode.toString());
		}
		for (JobShopNode childNode : nextNodeList)
		{
			childNode.printAll();
		}
	}
	//把完成順序print出來
	public List<JobShopNode> getCompleteList() {
		List<JobShopNode> completeList = new ArrayList<JobShopNode>();
		completeList.add(this);
		for (int i = 1; i <= complete; i++) {
			JobShopNode jobShopNode = findNextCompleteJobShopNode(i);
			if (jobShopNode != null)
				completeList.add(jobShopNode);
		}
		return completeList;
	}
	//依照machine把完成順序print出來
	public List<JobShopNode> getCompleteListWithMachine() {
		List<JobShopNode> completeList = getCompleteList();
		List<JobShopNode> completeListWithMachine = new ArrayList<JobShopNode>();
		completeList.remove(0);
		int machineId = 1;
		while (completeList.size() != completeListWithMachine.size()) {
			for (JobShopNode jobShopNode : completeList) {
				if (jobShopNode.getMachineId() == machineId)
					completeListWithMachine.add(jobShopNode);
			}
			++machineId;
		}
		return completeListWithMachine;
	}
	//查看兩節點是否一樣
	public boolean isEqual(JobShopNode jobShopNode) {
		if (jobId == jobShopNode.getJobId() && machineId == jobShopNode.getMachineId())
			return true;
		return false;
	}
	//查看是否為End Node
	public boolean isEndNode() {
		return nextNodeList.isEmpty();
	}
	/**
	 *  getter and setter
	 */
	public int getJobId() {
		return jobId;
	}
	
	public int getMachineId() {
		return machineId;
	}
	
	public int getProcessTime() {
		return processTime;
	}
	
	public void setProcessTime(int processTime) {
		this.processTime = processTime;
	}
	
	public int getComplete() {
		return complete;
	}

	public void setComplete(int complete) {
		this.complete = complete;
	}
	
}