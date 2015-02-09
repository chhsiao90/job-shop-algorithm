package my.JobShop.obj;

/***
 * Class: JobShopData
 * Description: Change the file data to the jobshop problem data 
 */
public class JobShopData {
	private int fromMachineId;
	private int fromJobId;
	private int toMachineId;
	private int toJobId;
	private int processTime;
	
	//Constructor
	public JobShopData(int fromMachineId, int fromJobId, int toMachineId,
			int toJobId, int processTime) {
		this.fromMachineId = fromMachineId;
		this.fromJobId = fromJobId;
		this.toMachineId = toMachineId;
		this.toJobId = toJobId;
		this.processTime = processTime;
	}
	//Constructor
	public JobShopData(int[] args) {
		this.fromMachineId = args[0];
		this.fromJobId = args[1];
		this.toMachineId = args[2];
		this.toJobId = args[3];
		this.processTime = args[4];
	}
	//取得母結點
	public JobShopNode getFromJobShopNode() {
		return new JobShopNode(fromMachineId, fromJobId, processTime);
	}
	//取得子結點
	public JobShopNode getToJobShopNode() {
		return new JobShopNode(toMachineId, toJobId, 0);
	}
	/**
	 *  getter and setter
	 */
	public int getFromJobId() {
		return fromJobId;
	}
	public int getFromMachineId() {
		return fromMachineId;
	}
	public int getToJobId() {
		return toJobId;
	}
	public int getToMachineId() {
		return toMachineId;
	}
	public int getProcessTime() {
		return processTime;
	}
	@Override
	public String toString() {
		return String.format("%d %d %d %d %d", fromMachineId, fromJobId, toMachineId, toJobId, processTime);
	}
	
}
