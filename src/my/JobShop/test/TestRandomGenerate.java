package my.JobShop.test;

import my.JobShop.util.RandomGenInput;

import org.junit.Test;

public class TestRandomGenerate {

	@Test
	public void testGenerate() {
		 int machineQty = 5;
		 int jobQty = 5;
		 int pathQty = 25;
		 RandomGenInput randomGenInput = new RandomGenInput(machineQty,
		 jobQty, pathQty);
		 randomGenInput.process();
		 randomGenInput.writeResultToFile("input.txt");

//		SingleMachineNode node1 = new SingleMachineNode(1, 4, 0, 8);
//		SingleMachineNode node2 = new SingleMachineNode(2, 2, 1, 12);
//		SingleMachineNode node3 = new SingleMachineNode(3, 6, 3, 11);
//		SingleMachineNode node4 = new SingleMachineNode(4, 5, 5, 10);
//		List<SingleMachineNode> nodes = new ArrayList<SingleMachineNode>();
//		nodes.add(node1);
//		nodes.add(node2);
//		nodes.add(node3);
//		nodes.add(node4);
//		List<Integer> order = new ArrayList<Integer>();
//		order.add(0);
//		order.add(0);
//		order.add(0);
//		order.add(0);
//		SingleMachineCalculationNew calculation = new SingleMachineCalculationNew(nodes);
//		Log log = new Log("singleMachine.txt");
//		log.clearLog();
//		calculation.setLog(log);
//		calculation.getLowerBoundRecur(order, 0, 0, 999);
//		int qty = calculation.getEliminateNodeQty();
//		int qty2 = calculation.getNodeQty();
	}
}
