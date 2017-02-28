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

	public enum Commands{max, help}
	private String _startingDirectory;
	private FileReader _in;
	private FileWriter _out;
	
	public ParseCSV(String startingDirectory, Commands command) {
		
		_startingDirectory = startingDirectory;
		
		switch (command) {
		
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
	
	private void max() {
		int fileNumber = 0;
		File dir = new File(_startingDirectory);
		LinkedList<File> files = (LinkedList<File>) FileUtils.listFiles(dir, new SuffixFileFilter(".CSV"), DirectoryFileFilter.DIRECTORY);
		String path =  _startingDirectory + "\\output.csv";
		try{
			_out = new FileWriter(path);
			
			for(int i = 0; i < files.size(); i++){
				println(files.get(i).getPath());
				parseFileMax(files.get(i).getAbsolutePath(), fileNumber);
				fileNumber += 1;
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

	private void parseFileMax(String absolutePath, int fileNumber) {
		try {
			String path = _startingDirectory + "\\output.CSV";
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
			double offset_d = Double.valueOf(offset);
			
			ArrayList<Double> dataList_d = new ArrayList<Double>();
			
			for (int i = 0; i < dataList.size(); i++){
				dataList_d.add(Double.valueOf(dataList.get(i)) * scale_d);
			}
			
			String max = max((double) 0, dataList_d);
			
			println("Max data value, scaled is: " + max);
			
			_out.write("," + max + "\n");
			
//			println("Scale: " + scale
//					+ ", Offset: " + offset + "\n"
//					+ dataList.toString());
//			_out.write("Scale: " + scale
//					+ ", Offset: " + offset + "\n"
//					+ dataList.toString());
			
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

	private String max(Double x, List<Double> dataList_d) {
		if (dataList_d.size() <= 1){
			return Double.toString(Math.max(x, dataList_d.get(0)));
		}else{
			return max (Math.max(x, dataList_d.get(0)), dataList_d.subList(1, dataList_d.size()));
		}
	}

	private void println(String x){
		System.out.println(x);
	}
	
	private void print(String x){
		System.out.print(x);
	}

}
