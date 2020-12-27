package com.shubham.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shubham.demo.models.LocationStats;
import com.shubham.demo.services.CoronaVirusDataService;

@Controller
public class HomeController {
	
	@Autowired
	CoronaVirusDataService coronaVirusDataService;
	
	@GetMapping("/")
	public String home(Model model) {
		
		List<LocationStats> allStats =  coronaVirusDataService.getAllStats();
		int totalReportedCases = allStats.stream().mapToInt(stat-> stat.getLatestTotalCases()).sum();
		int totalNewCases = allStats.stream().mapToInt(stat-> stat.getDiffFromPreviousDay()).sum();
		model.addAttribute("locationStats",allStats);   //since we dont have getter for the newStats so we need to create in that class
		model.addAttribute("totalReportedCases",totalReportedCases);
		model.addAttribute("totalNewCases",totalNewCases);
		
		return "home";
	}
}
