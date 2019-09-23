import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SortURLs {

	Map<String, HashMap<String, Integer>> map;

	SortURLs(String [] pathToFiles) {

		map = new TreeMap<>();
		for(String pathtoFile: pathToFiles) {
			parseFile(pathtoFile);
		}		
		printToConsole();
	
	}
	/*
	 * This method parses the contents in file and keeps the content to create
	 * report in the memory map (a treemap which has keys as day and value as 
	 * corresponding (urls, counts) in ascending order). 
	 * Since the number of days (k), is very less as compared to the unique urls (N),
	 * there would be minimal effect of sorting the number of days, 
	 * time complexity: O(N), as k<< N and O(N.log(k)) is nearly equal to O(N)
	 */
	void parseFile(String file) {

		Scanner s;
		try {
			s = new Scanner(new File(file));
			while (s.hasNextLine()) {

				String line = s.nextLine();
				String url[] = line.split("\\|");
				Date d = new Date(Long.parseLong(url[0]) * 1000);
				SimpleDateFormat dateFormatLocal = new SimpleDateFormat("MM-dd-YYYY");
				dateFormatLocal.setTimeZone(TimeZone.getTimeZone("GMT"));
				String day = String.valueOf(dateFormatLocal.format(d));

				if (!map.containsKey(day)) {
					map.put(day, new HashMap<String, Integer>());
				}
				map.get(day).put(url[1], map.get(day).getOrDefault(url[1], 0) + 1);

			}
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/*
	 * This method prints the report to the console.
	 * If there are 'k' days and a maximum of 'm' distinct hit rates in 
	 * one of the day, the complexity would be O(klog(k) ( mlog(m) + (n1 + n2+ ..nk)). 
	 * Given that the number of distinct hit count values (m) are much
	 * smaller than the number of unique urls m << N, and 
	 * number of days, k<<N, the time complexity would be almost
	 * O(N).
	 */
	void printToConsole() {

		for (String day : map.keySet()) {

			Map<String, Integer> umap = map.get(day);
			Map<String,Integer> sorted_umap = umap.entrySet().stream()
					.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
					.collect(Collectors.toMap(
					          Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			
			System.out.println(day);
			for(String url: sorted_umap.keySet()) {
				System.out.println(url + " "+ umap.get(url));
			}
			
		}
	}

	public static void main(String args[]) {

		if (args[0] == null)
			System.exit(0);
		new SortURLs(new String[] {args[0], args[1]});
	}
}

/*
 * The overall complexity of the program is
 * Time Complexity: O(N)
 * Space Complexity: O(N) 
 * 
 * Author: Sumedha Khatter
 */