package my.JobShop.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import my.JobShop.obj.FileFormatException;
import my.JobShop.obj.JobShopData;

/***
 * Class: FileProcess
 * Description: Use to access file
 *              Read the jobshop file
 *              And write the log
 */
public class FileProcess {

	public static List<JobShopData> getJobShopDataList(String filePath) throws FileFormatException {
		List<JobShopData> jobShopDataList = new ArrayList<JobShopData>();
		BufferedReader bufferReader;
		try {
			bufferReader = new BufferedReader(new FileReader(filePath));
			String readText;
			while ((readText = bufferReader.readLine()) != null)
			{
				String[] args = readText.trim().split(" ");
				JobShopData jobShopData = transJobShopData(args);
				if (jobShopData != null) {
					jobShopDataList.add(jobShopData);
				}
			}
			bufferReader.close();
		} catch (FileNotFoundException e) {
			throw new FileFormatException();
		} catch (IOException e) {
			throw new FileFormatException();
		} 
		
		return jobShopDataList;
	}
	
	private static JobShopData transJobShopData(String[] args) throws FileFormatException {
		try {
			if (args.length != 5)
				throw new FileFormatException();
			int[] dataArgs = new int[5];
			for (int i = 0; i < 5; i++)
				dataArgs[i] = Integer.parseInt(args[i]);
			JobShopData jobShopData = new JobShopData(dataArgs);
			return jobShopData;
		} catch (NumberFormatException e) {
			throw new FileFormatException();
		}
	}

	public static void writeStringToFile(String filePath, String msg) {
		BufferedWriter bufferedWriter;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(filePath, true));
			bufferedWriter.write(msg);
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (IOException e) {
		}
	}

	public static void replaceStringToFile(String filePath, String msg) {
		BufferedWriter bufferedWriter;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(filePath, false));
			bufferedWriter.write(msg);
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (IOException e) {
		}
	}
	
	public static void clearFileContent(String filePath) {
		BufferedWriter bufferedWriter;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(filePath));
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
		}
	}
}