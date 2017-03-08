package code;

public class Driver {

	private static String startingDirectory = "C:\\Users\\Jon\\Dropbox\\JT_School\\PHY 408\\Pulsed NMR\\Data\\Other Sample Data\\1000M CuSO4\\T2";
	/**
	 * The following main method only calls ParseCSV with its two arguments, the initial directory through which it shall recurse and the String Command it shall perform. 
	 * Currently, the only commands configured are 'max' and 'help'. Commands have format of all lower case Strings.
	 * @param args
	 */
	public static void main(String[] args) {
		new ParseCSV(startingDirectory, ParseCSV.Commands.t2_method2);

	}

}
