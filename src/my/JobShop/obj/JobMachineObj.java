package my.JobShop.obj;

public class JobMachineObj {

	private int jobId;
	private int machineId;
	
	public JobMachineObj(int jobId, int machineId) {
		this.jobId = jobId;
		this.machineId = machineId;
	}
	
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	public int getMachineId() {
		return machineId;
	}
	public void setMachineId(int machineId) {
		this.machineId = machineId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + jobId;
		result = prime * result + machineId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobMachineObj other = (JobMachineObj) obj;
		if (jobId != other.jobId)
			return false;
		if (machineId != other.machineId)
			return false;
		return true;
	}
	
	
}
