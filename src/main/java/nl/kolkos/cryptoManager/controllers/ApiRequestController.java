package nl.kolkos.cryptoManager.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(path="/api/request") 
public class ApiRequestController {
	
	
	@GetMapping(path="/test")
	public @ResponseBody Iterable<String> testApiAccess() {
		List<String> testje = new ArrayList<>();
		
		testje.add("Regel 1");
		testje.add("Regel 2");
		testje.add("Regel 3");
		
		return testje;
	}
	
}
