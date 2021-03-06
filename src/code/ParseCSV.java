package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;


public class ParseCSV {

	public enum Commands{max, help, t2_method1, t2_method2}
	private String _startingDirectory;
	private FileReader _in;
	private FileWriter _out;
	private ArrayList<Double> t1data = new ArrayList<Double>();
	
	public ParseCSV(String startingDirectory, Commands command) {
		
		_startingDirectory = startingDirectory;
		
		switch (command) {
		
		case t2_method2:
			println("Beginning to parse: \n\t'" + _startingDirectory + "'\n"
					+ "to determine the spin-spin decay curve contained therein and"
					+ "exporting the curve to output.csv in the same directory");
			t2_method2();
			break;
		
		case t2_method1:
			println("Beginning to parse: \n\t'" + _startingDirectory + "'\n"
					+ "recursivly for csv files and writing the max values of the\n"
					+ "spin-spin relaxation echos to output.csv in the same directory\n");
			t2_method1();
			break;
			
		case max:
			println("Beginning to parse: \n\t'" + _startingDirectory + "'\n"
					+ "recursivly for csv files and writing the max values to output.csv in the same directory\n");
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
			
			int index = min(t1data);
			for(int x = 0; x < index; x++){
				t1data.set(x, (((double) -1) * t1data.get(x)));
			}
			
			for(int i = 0; i < t1data.size(); i++){
				String val = Double.toString(t1data.get(i));
				_out.write("," + val + "\n");
				println("Max data value, scaled is: " + val);
			}
			
			println("index of min is: " + index + ", and the value at the index is: " + t1data.get(index));
			
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
	
	private int min(ArrayList<Double> list){
		double min = 100;
		double lastMin = 100;
		int index = 0;
		for(int i = 0; i < list.size(); i++){
			lastMin = min;
			min = Math.min(min, list.get(i));
			if(lastMin != min){
				index = i;
			}else if(min == list.get(i)){
				index = i;
			}
		}
		return index;
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
			
			double max_d = max_double((double) 0, dataList_d); 
			t1data.add(max_d);
			
			
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
	
	private double max_double(double x, List<Double> dataList_d){
		if (dataList_d.size() <= 1){
			return Math.max(x, dataList_d.get(0));
		}else{
			return max_double (Math.max(x, dataList_d.get(0)), dataList_d.subList(1, dataList_d.size()));
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
	
	private void t2_method2(){
		File dir = new File(_startingDirectory);
		LinkedList<File> files = (LinkedList<File>) FileUtils.listFiles(dir, new SuffixFileFilter(".CSV"), DirectoryFileFilter.DIRECTORY);
		String outpath = _startingDirectory + "\\output.csv";
		try{
			_out = new FileWriter(outpath);
			
			for(int i = 0; i < files.size(); i++){
				println(files.get(i).getPath());
				parseFile_t2_method2(files.get(i).getAbsolutePath());
			}
			
		} catch (IOException e) {
			println("File at " + outpath + "Could not be written to: IOException");
		}finally{
			try {
				_out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void parseFile_t2_method2(String path){
		try {
			Scanner ui = new Scanner(System.in);
			println("Please enter the delay time for this file (in sec).");
			double delaytime = ui.nextDouble();
			_in = new FileReader(path);
			int c;
			int commaCount = 0;
			int lineCount = 0;
			String data_y = "";
			String data_x = "";
			String scale_y = "";
			String scale_x = "";
			String offset = "";
			ArrayList<String> dataList_y = new ArrayList<String>();
			ArrayList<String> dataList_x = new ArrayList<String>();
			
			while((c = _in.read()) != -1){
				char cur = (char) c;
				
				switch (cur){
				case ',':
					commaCount += 1;
					break;

				case '\n':
					lineCount += 1;
					dataList_y.add(data_y);
					dataList_x.add(data_x);
					data_y = "";
					data_x = "";
					commaCount = 0;
					break;
				
				case ' ':
					break;
					
				default:
					if(commaCount ==3){
						data_x = data_x + cur;
					}
					if(commaCount == 4){
						data_y = data_y + cur; 
					}
					if(commaCount == 1){
						if(lineCount == 8){
							scale_y = scale_y + cur;
						}if(lineCount == 9){
							offset = offset + cur;
						}if(lineCount ==11){
							scale_x = scale_x + cur;
						}
					}
					break;
				}
			}
			double scale_yd = Double.valueOf(scale_y);
			double scale_xd = Double.valueOf(scale_x);
			double offset_d = Double.valueOf(offset); /* not yet used because not yet understood */
			
			ArrayList<Double> dataList_yd = new ArrayList<Double>();
			ArrayList<Double> dataList_xd = new ArrayList<Double>();
			
			for (int i = 0; i < dataList_y.size(); i++){
				dataList_yd.add(Double.valueOf(dataList_y.get(i)) * scale_yd);
				dataList_xd.add(Double.valueOf(dataList_x.get(i)) * scale_xd);
			}
			
			curve_t2_method2(dataList_yd, dataList_xd, delaytime);
		} catch (FileNotFoundException e) {
			println("File at " + path + "Could not be parsed: FileNotFoundException");
		} catch (IOException e) {
			println("File at " + path + "Could not be parsed: IOException");
		}catch (InputMismatchException e){
			println("You did not input a propper delay time.\n\n Exiting...");
			System.exit(0);
		}finally{
			try {
				_in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void curve_t2_method2(ArrayList<Double> list_y, ArrayList<Double> list_x, double delaytime){
		double threshold = .15; /* threshold below which values are considered zero. Aids in the reset of finding the local max */
		double curMax = 0;
		int indexOfMax = 0;
		double comp = 0;
		int count = 0;
		int state = 0; /* 0 = above threshold, 1 = below threshold*/
		for(int i = 0; i< list_y.size(); i++){
			if(state == 0 && (comp = list_y.get(i)) > threshold){
				if (curMax < comp){
					curMax = comp;
					indexOfMax = i;
				}
			}else if(state == 0 && (comp = list_y.get(i)) < threshold){
				state = 1;
				if (curMax < comp){
					curMax = comp;
					indexOfMax = i;
				}
				
			}else if(state == 1 && (comp = list_y.get(i)) > threshold){
				state = 0;
				if (curMax < comp){
					curMax = comp;
					indexOfMax = i;
				}
				try {
					_out.write(Double.toString((delaytime) * count) + "," + Double.toString(list_y.get(indexOfMax)) + "\n");
					println(Double.toString((delaytime) * count) + "," + Double.toString(list_y.get(indexOfMax)) + "\n");
					curMax = 0;
					comp = 0;
					count += 1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (curMax < comp){
					curMax = comp;
					indexOfMax = i;
				}
			}
		}
		println("This operation produced " + count + " data points in output.csv");
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
