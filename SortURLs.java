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
		for(String pathtoFile: pathToFiles)
			parseFile(pathtoFile);
		printToConsole();
	}

	/*
	 * This method parses the contents in file and keeps the content to create
	 * report in the memory map (a treemap which has keys as day and value as 
	 * corresponding (urls, counts) in ascending order). 
	 * Since there are N total lines in  all files
	 * A treemap takes O(log N) for get and put methods.
	 * A hashmap takes O(1) for get and put operations.
	 * time complexity: Big O(N log(N))
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
	 * Explanation: If there are 'k' days and a maximum of 'm' urls in 
	 * one of the day, the complexity would be O(klog(k). mlog(m)). 
	 * Think of this complexity as formed with 2 terms klog(k) and other mlog(m) (time to
	 * sort m elements)
	 * If we maximize, k, i.e. k = n, which signifies, all the lines in the file
	 * have unique date, and their corr. map would have 1 entry. So 
	 * complexity will become O(Nlog(N))
	 * If we maximize m, Nlog(N), all urls in 1 date, time complexity
	 * becomes O(Nlog(N))
	 * For the case where, k < N and m < N; klog(k).mlog(m) < N.log(N)
	 * Thus for this method, complexity is big O(N.log(N))
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
 * The overall Big O time complexity of the program is
 * O(N) for parseFile and O(N.log(N)) for printToConsole.
 * Hence for the entire functionality, we can write it as 
 * Time Complexity: O(N.log(N))
 * Space Complexity: O(N) 
 * 
 * Author: Sumedha Khatter
 */