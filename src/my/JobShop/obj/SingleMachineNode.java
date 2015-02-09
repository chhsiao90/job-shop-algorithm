package my.JobShop.obj;

import java.util.ArrayList;
import java.util.List;

/***
 * Class: SingleMachineNode
 * Description: The node for the algorithm of single machine
 */
public class SingleMachineNode {
	private int jobId;	//Job Id
	private int processTime;	//Process Time
	private int releaseTime;	//Release Time
	private int dueDate;	//Due Date
	private int endTime;	//End Time 若為0則該job還未完成
	private int actualTime;		//已完成時間 若等於Process Time則完成
	private List<RouteTime> routeTimeList = new ArrayList<RouteTime>();
	
	public SingleMachineNode(int jobId, int processTime, int releaseTime, int dueDate) {
		this.jobId = jobId;
		this.processTime = processTime;
		this.releaseTime = releaseTime;
		this.dueDate = dueDate;
		this.endTime = 0;
		this.actualTime = 0;
	}
	
	public SingleMachineNode(SingleMachineNode node) {
		this.jobId = node.getJobId();
		this.processTime = node.getProcessTime();
		this.releaseTime = node.getReleaseTime();
		this.dueDate = node.getDueDate();
		this.endTime = node.getEndTime();
		this.actualTime = node.getActualTime();
	}

	//取得該job是否完成
	public boolean isCompleted() {
		return (actualTime == processTime || endTime != 0);
	}
	//取得已完成的job的cost
	public int getCost() {
		return (!isCompleted() ? 999 : (dueDate >= endTime ? 0 : endTime - dueDate));
	}
	//複製結點
	public SingleMachineNode copyNode() {
		SingleMachineNode node = new SingleMachineNode(this);
		return node;
	}
	@Override
	public String toString() {
		return "(" + jobId + ","
				+ processTime + "," + releaseTime + ","
				+ dueDate + ")";
	}

	/**
	 *  Getter and setter
	 */
	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public int getProcessTime() {
		return processTime;
	}

	public void setProcessTime(int processTime) {
		this.processTime = processTime;
	}

	public int getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(int releaseTime) {
		this.releaseTime = releaseTime;
	}

	public int getDueDate() {
		return dueDate;
	}

	public void setDueDate(int dueDate) {
		this.dueDate = dueDate;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getActualTime() {
		return actualTime;
	}

	public void setActualTime(int actualTime) {
		this.actualTime = actualTime;
	}

	public List<RouteTime> getRouteTimeList() {
		return routeTimeList;
	}
}