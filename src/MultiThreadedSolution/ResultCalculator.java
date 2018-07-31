package MultiThreadedSolution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.RecursiveAction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ResultCalculator extends RecursiveAction {

	// Constants
	public int[] daysInMonth = new int[] { 0, 31, 28, 31, 1 };
	private final String HOST = "http://localhost:9999";

	// variables to store results
	public static int[] hours = new int[24];
	public static int[] days = new int[8];
	public static HashMap<String, Integer> tuesday = new HashMap<String, Integer>();
	public static HashMap<Integer, HashMap<String, Integer>> friday = new HashMap<Integer, HashMap<String, Integer>>();
	public static int[][] hoursAndDays = new int[8][24];

	// Variables to maintain concurrency
	private static int locationID = 10;
	private int location = 0;

	@Override
	protected void compute() {
		// TODO Auto-generated method stub

		// everythread works for a single location
		if (location > locationID) {

			// get all member details for the location
			HashMap<String, String> membersAgrementType = getMembers();

			// for every month
			for (int month = 1; month <= 4; month++) {
				// for every date
				for (int date = daysInMonth[month]; date >= 1; date--) {

					// get all checkin details for the day
					JSONArray jsonArray = getCheckIn(month, date, location);

					Iterator it = jsonArray.iterator();
					while (it.hasNext()) {

						JSONObject jsonObject = (JSONObject) it.next();
						Iterator iterator = jsonObject.keySet().iterator();
						
							String key = (String) iterator.next();
							String checkinTime = (String) jsonObject.get(key);
							key = (String) iterator.next();
							String agreement = membersAgrementType.get(String.valueOf(jsonObject.get(key)));
							
							// Using calendar class to derive day of the week from the date
							Calendar calendar = Calendar.getInstance();

							calendar.set(Integer.parseInt(checkinTime.substring(0, 4)), month, date);
							int day = calendar.get(Calendar.DAY_OF_WEEK);
							
							
							//aggregating values for question based on most agreement
							//Tuesday
							if(day == 3) {
								
								synchronized (tuesday) {
									if (tuesday.containsKey(agreement)) {
										tuesday.put(agreement, tuesday.get(agreement) + 1);
									} else {
										tuesday.put(agreement, 1);
									}
								}
							}
							//Friday
							if(day == 6) {
								synchronized (friday) {
									HashMap<String, Integer> temp = friday.get(location);
									if (temp == null) {
										temp = new HashMap<String, Integer>();
										temp.put(agreement, 1);
										friday.put(location, temp);
									} else if (temp.containsKey(agreement)) {
										temp.put(agreement, temp.get(agreement) + 1);
									} else {
										temp.put(agreement, 1);
									}
	
								}
							}

								// when key is checkin date aggregating values for question based on frequency
								

							synchronized (days) {
									days[day]++;
								}

								int hour = Integer.parseInt(checkinTime.substring(11, 13));
								synchronized (hours) {
									hours[hour]++;
								}

								synchronized (hoursAndDays) {
									hoursAndDays[day - 1][hour]++;
								}
							}
						}
					
				}
			}
			// breaking tasks for threads based on threads
			if (locationID > 0) {
				ResultCalculator rs = new ResultCalculator();

				location = locationID;
				locationID--;

				rs.fork();
				compute();
				rs.join();// waiting for all threads to be done
			}
		}

	// method to fetch and tranform checkin data from the server
	private JSONArray getCheckIn(int month, int date, int location) {

		URL url;
		HttpURLConnection conn;
		String output;
		StringBuilder sb = new StringBuilder();

		try {
			url = new URL(HOST + "/checkins?locationId=" + location + "&dateToCheck=2018-" + month + "-" + date);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException(
						"Failed : HTTP error code : " + conn.getResponseCode() + month + date + location);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			while ((output = br.readLine()) != null) {
				sb.append(output);
			}

			JSONParser parser = new JSONParser();
			JSONArray json = (JSONArray) parser.parse(sb.substring(8, sb.length() - 14));

			conn.disconnect();

			return json;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	// method to fetch and tranform checkin data from the server
	private HashMap<String, String> getMembers() {
		URL url;
		HttpURLConnection conn;
		String output;
		StringBuilder sb = new StringBuilder();
		HashMap<String, String> members = new HashMap<String, String>();

		try {
			url = new URL(HOST + "/members?locationId=" + (location - 1));
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			while ((output = br.readLine()) != null) {
				sb.append(output);
			}

			conn.disconnect();

			JSONParser parser = new JSONParser();
			JSONArray jsonArray = (JSONArray) parser.parse(sb.substring(8, sb.length() - 14));

			Iterator it = jsonArray.iterator();
			while (it.hasNext()) {
				JSONObject jsonObject = (JSONObject) it.next();
				Iterator iterator = jsonObject.keySet().iterator();
				while (iterator.hasNext()) {
					// System.out.println( jsonObject.get(iterator.next()));

					String agreement = String.valueOf(jsonObject.get(iterator.next()));
					members.put(String.valueOf(jsonObject.get(iterator.next())), agreement);
				}
			}

			return members;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
