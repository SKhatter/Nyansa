import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.TreeMap;

public class SortURLs {

	static int SMALLF_NO_OF_LINES = 20000; // the number of lines in small file
	static String SMALL_FILE_NAME = "small_file";
	static String SMALL_FILE_EXT = ".txt";
	Map<String, List<String>> map;
	TreeMap<String, PriorityQueue<String[]>> result;

	
	SortURLs(String pathToFile) {

		map = new HashMap<>();
		result = new TreeMap<>();
		processFile(pathToFile);

	}

	
	/*
	 * The method splits the file into chunks if the 
	 * size of file is huge. Each chunk is then 
	 * parsed individually and the required
	 * data is kept in map (memory). 
	 */
	void processFile(String filename) {
		
		int lines =  countLines(filename);
		if(lines > SMALLF_NO_OF_LINES) {
			int noOfFiles = lines / SMALLF_NO_OF_LINES + (lines % SMALLF_NO_OF_LINES > 1 ? 1 : 0);
			splitFile(filename, noOfFiles);
			for (int i = 0; i < noOfFiles; i++) {
				File temp = new File(SMALL_FILE_NAME + String.valueOf(i) + SMALL_FILE_EXT);
				parseFile(temp);
				temp.delete();
			}
		} else {
			parseFile(new File(filename));
		}
		printToConsole();
	} 
	
	/*
	 * This method returns the number of
	 * lines in the file.
	 * Time Complexity: O(N) where N is the lines in file
	*/
	int countLines(String filename) {
		
		BufferedReader reader;
		int lines = 0;
		try {
			reader = new BufferedReader(new FileReader(filename));
			while (reader.readLine() != null) 
				lines++;
			reader.close();
		} 
		catch (FileNotFoundException e) {	
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	
	/*
	 * This method splits the file into
	 *  multiple files. 
	 * 
	*/
	int splitFile(String filename, int noOfFiles) {

		FileInputStream fstream;
		int index = 0;
		try {
			fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			while (index < noOfFiles) {
				writeToFile(br, index++);
			}
			in.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return index;

	}

	/*
	 * This method creates and writes the content to small files.
	*/
	void writeToFile(BufferedReader br, int index) throws IOException {
		FileWriter fstream1 = new FileWriter(SMALL_FILE_NAME + index + SMALL_FILE_EXT);
		String strLine;
		BufferedWriter out = new BufferedWriter(fstream1);
		for(int i = 0; i < SMALLF_NO_OF_LINES; i++) {
			strLine = br.readLine();
			if(strLine == null) {
				break;
			}
			out.write(strLine);
			if(i != SMALLF_NO_OF_LINES) {
				out.newLine();
			}
		}
		
		out.close();
	}

	
	/*
	 * This method parses the contents in file
	 * and keeps the content to create report 
	 * in the memory (map). The map has a url as 
	 * key and value is a list of all dates
	 * The first value of the list is the smallest.
	 * Time Complexity: O(N) 
	 * Explanation: while loop scans N lines, hence O(N)
	 * map.containsKey(x) takes O(1) and compareTo takes O(1)
	 * Hence overall time complexity is O(N)
	 * */
	void parseFile(File file) {

		Scanner s;
		try {
			s = new Scanner(file);
			while (s.hasNextLine()) {

				String line = s.nextLine();
				String url[] = line.split("\\|");

				Date d = new Date(Long.parseLong(url[0]) * 1000);

				SimpleDateFormat dateFormatLocal = new SimpleDateFormat("MM-dd-YYYY");
				dateFormatLocal.setTimeZone(TimeZone.getTimeZone("GMT"));

				if (!map.containsKey(url[1])) {
					List<String> l = new ArrayList<String>();
					l.add(dateFormatLocal.format(d));
					map.put(url[1], l);
				} else if (map.get(url[1]).get(0).compareTo(dateFormatLocal.format(d)) >= 0) {
					map.get(url[1]).add(0, dateFormatLocal.format(d));
				} else {
					map.get(url[1]).add(String.valueOf(dateFormatLocal.format(d)));
				}

			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	
	
	/*
	 * This method prints the report to the console
	 * Time Complexity: O(NlogN.logN)
	 * Explanation: The worst case could be 
	 * all the lines in file have different url, and 
	 * have same date. Each insertion and pop from 
	 * priority queue takes O(log N).Each get from 
	 * a treemap takes O(log N)
	 * */
	void printToConsole() {
		
		for (String k : map.keySet()) {

			List<String> l = map.get(k);
			String min = l.get(0);
			if (!result.containsKey(min)) {
				PriorityQueue<String[]> pq = new PriorityQueue<String[]>((a, b) -> b[1].compareTo(a[1]));
				result.put(min, pq);
			}
			result.get(min).add(new String[] { k, String.valueOf(l.size()) });
		}

		for (String date : result.keySet()) { 

			System.out.println(date);
			while (!result.get(date).isEmpty()) {
				String url[] = result.get(date).poll();
				System.out.println(url[0] + " " + url[1]);
			}
		}

	}

	
	
	public static void main(String args[]) {

		if(args[0] == null)
			System.exit(0); 
		new SortURLs(args[0]);
	}
}



/* The overall Big O time complexity of the program is 
* O(N (log N)^2)
* Space Complexity: O(N)
* Author: Sumedha Khatter
*/
