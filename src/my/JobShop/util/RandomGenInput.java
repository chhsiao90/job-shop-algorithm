package my.JobShop.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import my.JobShop.obj.JobMachineObj;
import my.JobShop.obj.JobShopData;

public class RandomGenInput {

	private int machineQty;
	private int jobQty;
	private int pathQty;
	private List<JobMachineObj> nowList;
	private Map<Integer, List<JobMachineObj>> allMap;
	private List<JobShopData> jobShopDatas;
	private Random random = new Random();
	private static final int MAX_PROCESS_TIME = 10;

	public RandomGenInput(int machineQty, int jobQty, int pathQty) {
		this.machineQty = machineQty;
		this.jobQty = jobQty;
		this.pathQty = pathQty;
	}

	public void process() {
		initial();
		setFirstPath();
		setPath();
		setFinalPath();
	}

	private void initial() {
		nowList = new ArrayList<JobMachineObj>();
		allMap = new HashMap<Integer, List<JobMachineObj>>();
		jobShopDatas = new ArrayList<JobShopData>();
		for (int i = 1; i <= jobQty; i++) {
			List<JobMachineObj> objs = new ArrayList<JobMachineObj>();
			allMap.put(i, objs);
			for (int j = 1; j <= machineQty; j++) {
				JobMachineObj obj = new JobMachineObj(i, j);
				objs.add(obj);
			}
		}
	}

	private void setFirstPath() {
		JobMachineObj from = new JobMachineObj(0, 0);
		for (int jobId = 1; jobId <= jobQty; jobId++) {
			int machineId = random.nextInt(machineQty) + 1;
			JobMachineObj to = new JobMachineObj(jobId, machineId);
			nowList.add(to);
			allMap.get(jobId).remove(to);
			putDataIntoNode(from, to, 0);
		}
	}

	private void setPath() {
		for (int nowPath = 0; nowPath < pathQty - jobQty; nowPath++) {
			int index = random.nextInt(nowList.size());
			JobMachineObj from = nowList.get(index);
			int jobId = from.getJobId();
			if (allMap.get(jobId).size() == 0) {
				nowPath--;
				continue;
			}
			int index2 = random.nextInt(allMap.get(jobId).size());
			JobMachineObj to = allMap.get(jobId).get(index2);
			nowList.remove(index);
			nowList.add(to);
			allMap.get(jobId).remove(index2);
			putDataIntoNode(from, to);
		}
	}
	
	private void setFinalPath() {
		JobMachineObj to = new JobMachineObj(jobQty + 1, machineQty + 1);
		for (JobMachineObj from : nowList) {
			putDataIntoNode(from, to);
		}
	}

	private void putDataIntoNode(JobMachineObj from, JobMachineObj to, int processTime) {
		JobShopData jobShopData = new JobShopData(from.getMachineId(),
				from.getJobId(), to.getMachineId(), to.getJobId(), processTime);
		jobShopDatas.add(jobShopData);
	}

	private void putDataIntoNode(JobMachineObj from, JobMachineObj to) {
		int processTime = random.nextInt(MAX_PROCESS_TIME) + 1;
		putDataIntoNode(from, to, processTime);
	}
	
	public void writeResultToFile(String filePath) {
		StringBuffer stringBuffer = new StringBuffer();
		for (JobShopData jobShopData : jobShopDatas) {
			stringBuffer.append(jobShopData.toString()).append("\r\n");
		}
		String msg = stringBuffer.substring(0, stringBuffer.length() - 2);
		FileProcess.replaceStringToFile(filePath, msg);
	}

	public int getMachineQty() {
		return machineQty;
	}

	public void setMachineQty(int machineQty) {
		this.machineQty = machineQty;
	}

	public int getJobQty() {
		return jobQty;
	}

	public void setJobQty(int jobQty) {
		this.jobQty = jobQty;
	}

	public int getPathQty() {
		return pathQty;
	}

	public void setPathQty(int pathQty) {
		this.pathQty = pathQty;
	}

}
