package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;


public class ParseCSV {

	public enum Commands{max, help, t2_method1}
	private String _startingDirectory;
	private FileReader _in;
	private FileWriter _out;
	
	public ParseCSV(String startingDirectory, Commands command) {
		
		_startingDirectory = startingDirectory;
		
		switch (command) {
		
		case t2_method1:
			println("Beginning to parse: \n\t'" + _startingDirectory + "'\n"
					+ "recursivly for csv files and writing the max values of the\n"
					+ "spin-spin relaxation echos to " + _startingDirectory + ".csv in the same directory\n");
			t2_method1();
			break;
			
		case max:
			println("Beginning to parse: \n\t'" + _startingDirectory + "'\n"
					+ "recursivly for csv files and writing the max values to " + _startingDirectory + ".csv in the same directory\n");
			max();
			break;
			
		case help:
			println("You have reached the default/help command for ParseCSV.\n"
					+ "\tCurrently encoded commands are:\n"
					+ "\t\tmax: outputs a csv of the max values in each csv file.\n"
					+ "\t\thelp: prints this same output.");
			System.exit(0);
		
		default:
			println("You have reached the default/help command for ParseCSV.\n"
					+ "\tCurrently encoded commands are:\n"
					+ "\t\tmax: outputs a csv of the max values in each csv file.\n"
					+ "\t\thelp: prints this same output.");
			System.exit(0);
			
		}
		println("\nDone");
		System.exit(0);
	}
	
	/**
	 * Private void max function that collects a list of all files in the starting directory that have the suffix '.CSV'.
	 * It then instantiates the output writer.
	 * It then iterates through the above file list, calling 'parseFIleMax' on each.
	 * It finishes by cleaning up (closing the file writer)
	 * 
	 * This function writes to 'output.csv'
	 */
	private void max() {
		File dir = new File(_startingDirectory);
		LinkedList<File> files = (LinkedList<File>) FileUtils.listFiles(dir, new SuffixFileFilter(".CSV"), DirectoryFileFilter.DIRECTORY);
		String path =  _startingDirectory + "\\output.csv";
		try{
			_out = new FileWriter(path);
			
			for(int i = 0; i < files.size(); i++){
				println(files.get(i).getPath());
				parseFileMax(files.get(i).getAbsolutePath());
			}
			
		} catch (IOException e) {
			println("File at " + path + "Could not be written to: IOException");
		}finally{
			try {
				_out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void parseFileMax(String absolutePath) {
		try {
			_in = new FileReader(absolutePath);
			int c;
			int commaCount = 0;
			int lineCount = 0;
			String data = "";
			String scale = "";
			String offset = "";
			ArrayList<String> dataList = new ArrayList<String>();
			
			while((c = _in.read()) != -1){
				char cur = (char) c;
				
				switch (cur){
				case ',':
					commaCount += 1;
					break;

				case '\n':
					lineCount += 1;
					dataList.add(data);
					data = "";
					commaCount = 0;
					break;
				
				case ' ':
					break;
					
				default:
					if(commaCount == 4){
						data = data + cur; 
					}
					if(commaCount == 1){
						if(lineCount == 8){
							scale = scale + cur;
						}if(lineCount == 9){
							offset = offset + cur;
						}
					}
					break;
				}
			}
			double scale_d = Double.valueOf(scale);
			double offset_d = Double.valueOf(offset); /* not yet used because not yet understood */
			
			ArrayList<Double> dataList_d = new ArrayList<Double>();
			
			for (int i = 0; i < dataList.size(); i++){
				dataList_d.add(Double.valueOf(dataList.get(i)) * scale_d);
			}
			
			String max = max((double) 0, dataList_d);
			
			println("Max data value, scaled is: " + max);
			
			_out.write("," + max + "\n");
			
			_out.flush();
		} catch (FileNotFoundException e) {
			println("File at " + absolutePath + "Could not be parsed: FileNotFoundException");
		} catch (IOException e) {
			println("File at " + absolutePath + "Could not be parsed: IOException");
		}finally{
			try {
				_in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * This is a recursive function that searches a list for the maximum double value and returns the String representation of that value.  
	 * 
	 * @param x, currant maximum value 
	 * @param dataList_d, list left to be searched through
	 * @return String representation of maximum value in set
	 */
	private String max(Double x, List<Double> dataList_d) {
		if (dataList_d.size() <= 1){
			return Double.toString(Math.max(x, dataList_d.get(0)));
		}else{
			return max (Math.max(x, dataList_d.get(0)), dataList_d.subList(1, dataList_d.size()));
		}
	}

	private void t2_method1(){
		File dir = new File(_startingDirectory);
		LinkedList<File> files = (LinkedList<File>) FileUtils.listFiles(dir, new SuffixFileFilter(".CSV"), DirectoryFileFilter.DIRECTORY);
		String path =  _startingDirectory + "\\output.csv";
		try{
			_out = new FileWriter(path);
			
			for(int i = 0; i < files.size(); i++){
				println(files.get(i).getPath());
				parseFile_t2_method1(files.get(i).getAbsolutePath());
			}
			
		} catch (IOException e) {
			println("File at " + path + "Could not be written to: IOException");
		}finally{
			try {
				_out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void parseFile_t2_method1(String path){
		try {
			_in = new FileReader(path);
			int c;
			int commaCount = 0;
			int lineCount = 0;
			String data = "";
			String scale = "";
			String offset = "";
			ArrayList<String> dataList = new ArrayList<String>();
			
			while((c = _in.read()) != -1){
				char cur = (char) c;
				
				switch (cur){
				case ',':
					commaCount += 1;
					break;

				case '\n':
					lineCount += 1;
					dataList.add(data);
					data = "";
					commaCount = 0;
					break;
				
				case ' ':
					break;
					
				default:
					if(commaCount == 4){
						data = data + cur; 
					}
					if(commaCount == 1){
						if(lineCount == 8){
							scale = scale + cur;
						}if(lineCount == 9){
							offset = offset + cur;
						}
					}
					break;
				}
			}
			double scale_d = Double.valueOf(scale);
			double offset_d = Double.valueOf(offset); /* not yet used because not yet understood */
			
			ArrayList<Double> dataList_d = new ArrayList<Double>();
			
			for (int i = 0; i < dataList.size(); i++){
				dataList_d.add(Double.valueOf(dataList.get(i)) * scale_d);
			}
			String max = "Failed to find amplitude of echo (From t2_method1_main)";
			
			max = max((double) 0, dataList_d);
			
			
			println("Max data value, scaled is: " + max);
			
			_out.write("," + max + "\n");
			
			_out.flush();
		} catch (FileNotFoundException e) {
			println("File at " + path + "Could not be parsed: FileNotFoundException");
		} catch (IOException e) {
			println("File at " + path + "Could not be parsed: IOException");
		}finally{
			try {
				_in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Helper function to make system.out.println use easier.
	 * @param x, string to be passed to system.out.println
	 */
	private void println(String x){
		System.out.println(x);
	}
	
	/**
	 * Helper function to make system.out.print use easier.
	 * @param x, string to be passed to system.out.print
	 */
	private void print(String x){
		System.out.print(x);
	}

}
