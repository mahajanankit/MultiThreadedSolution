package MultiThreadedSolution;

import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;

public class Main {

	public static void main(String[] args) {

		// Starting Threads
		final ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
		pool.invoke(new ResultCalculator());

		// Analyse data
		
		max2DArray(ResultCalculator.hoursAndDays);
		
		int max = maxArray(ResultCalculator.hours);
		StringBuilder sb = new StringBuilder(getTime(max));
		sb.append(" is the busiest hour in a day with ");
		sb.append(ResultCalculator.hours[max]);
		System.out.println(sb.toString());
		
		
		max = maxArray(ResultCalculator.days);
		sb = new StringBuilder(getDay(max));
		sb.append(" is the busiest day in the week with ");
		sb.append(ResultCalculator.days[max]);
		System.out.println(sb.toString());
		
		
		String highest = mostAgreementsOnTuesday(ResultCalculator.tuesday);
		sb = new StringBuilder(highest);
		sb.append(" members checkin the most on tuesdays with ");
		sb.append(ResultCalculator.tuesday.get(highest));
		System.out.println(sb.toString());		
		
		mostAgreementsOnFridayByLocation(ResultCalculator.friday);
		
	}
	

	private static void max2DArray(int[][] hoursAndDays) {
		// TODO Auto-generated method stub

		int d = 0, h = 0;
		for (int i = 0; i < hoursAndDays.length; i++) {
			for (int j = 0; j < hoursAndDays[0].length; j++) {
				if (hoursAndDays[d][h] < hoursAndDays[i][j]) {
					d = i;
					h = j;
				}
			}
		}
		
		StringBuilder sb = new StringBuilder(getDay(d));
		sb.append(" ");
		sb.append(getTime(h));	
		sb.append(" is the busiest hour in a week with ");
		sb.append(hoursAndDays[d][h]);
		System.out.println(sb.toString());
	}
	

	private static int maxArray(int[] arr) {
		// TODO Auto-generated method stub
		int max = 0;

		for (int i = 0; i < arr.length; i++) {
			if (arr[max] < arr[i]) {
				max = i;
			}
		}
		return max;

	}
	
	
	public static String mostAgreementsOnTuesday(HashMap<String, Integer> map) {
		// TODO Auto-generated method stub
		String max = "";

		for (String key : map.keySet()) {
			if(max == "") max = key;
			else if (map.get(max) < map.get(key)) {
				max = key;
			}
		}
		
		return max;
	}

	
	public static void mostAgreementsOnFridayByLocation(HashMap<Integer, HashMap<String, Integer>> map) {
		// TODO Auto-generated method stub
		HashMap<String, Integer> temp;
		System.out.println("Agreementtype with most checkins on fridays:");

		for (int i = 1; i < 10; i++) {
			temp = map.get(i);
			String max = mostAgreementsOnTuesday(temp);
			System.out.println("\t Location " + i + ": " + max + " with " + temp.get(max));
		}
	}
	
	
	private static String getDay(int dayOfWeek) {
		switch (dayOfWeek) {
		case 1:
			return "Sunday";
		case 2:
			return "Monday";
		case 3:
			return "Tuesday";
		case 4:
			return "Wednesday";
		case 5:
			return "Thursday";
		case 6:
			return "Friday";
		case 7:
			return "Saturday";
		}
		return "";
	}

	
	private static String getTime(int hours) {
		StringBuilder ap = new StringBuilder(" am");
		if (hours > 12) {
			ap.replace(1, 2, "p");
			hours -= 12;
		}

		ap.insert(0, hours);

		return ap.toString();

	}

}