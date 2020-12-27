package com.shubham.demo.services;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.shubham.demo.models.LocationStats;

@Service   //its basically saying hey spring after creating this instance of a (service)class please construct below method or execute below method//
public class CoronaVirusDataService {

	private static String VIRUS_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStats> allStats = new ArrayList<>();//here we are created a list of all locationstats
	public List<LocationStats> getAllStats() {
		return allStats;
	}

	public void setAllStats(List<LocationStats> allStats) {
		this.allStats = allStats;
	}

	
	@PostConstruct
	@Scheduled(cron = "* * 1 * * * ") //we also need to mentioned it on main class too //we mentioned * so that it can update in every sec we also can mentioned sec//our data is update time to time if we do not reload the data in our application itll be outdate inevery update so we use this to make this app to update on some schedule//
	public void fetchData() throws Exception{
		List<LocationStats> newStats = new ArrayList<>();
				
		HttpClient client = HttpClient.newHttpClient(); //this is the way u call http call//
		HttpRequest request = HttpRequest.newBuilder()   //its allow us to buid pattern
		.uri(URI.create(VIRUS_DATA_URL)) //itll convert string into uri
		.build();
		
		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString()); //we basically doing here to sending a req with client and tell take the body and return it as string
		
		
		//WE ARE USING HEADER AUTO DETCTION METHOD WHICH WE HAVE PICKED FROM  COMMONS.APACHE.COM/ SERGUID OF CSV LIBRARY IT DETECT THE HEADER OF OUR FILE WHICH WE HAVE FETCHING AND SETTING IT AS REQUERED FORMATE
		StringReader csvBodyReader = new StringReader(httpResponse.body());//strignReader is a instance of a Reader class u can see in usergid on the site which parses string//
		
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationStats locationStat = new LocationStats();
		    locationStat.setStates(record.get("Province/State"));
		    locationStat.setCountry(record.get("Country/Region"));
		    int latestCases = Integer.parseInt(record.get(record.size()-1));
		    int previousCases = Integer.parseInt(record.get(record.size()-2));
		    locationStat.setLatestTotalCases(latestCases); //why this is bcoz our record of columns keeps increasing on daily basis//
		    locationStat.setDiffFromPreviousDay(latestCases-previousCases);
		    
		    
		    
		   newStats.add(locationStat);
		}
		this.allStats=newStats;
	}

	
}
