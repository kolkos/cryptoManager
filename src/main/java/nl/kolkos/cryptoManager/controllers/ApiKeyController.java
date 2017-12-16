package nl.kolkos.cryptoManager.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.kolkos.cryptoManager.ApiKey;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.services.ApiKeyService;
import nl.kolkos.cryptoManager.services.UserService;

@Controller    // This means that this class is a Controller
@RequestMapping(path="/api") // This means URL's start with /demo (after Application path)
public class ApiKeyController {
		
	@Autowired
	private UserService userService;
	
	@Autowired
	private ApiKeyService apiKeyService;
			
	
	// the api generation form
	@GetMapping("/manageKeys")
    public String apiKeyForm(@RequestParam(value="message", required=false) String message,
    		@RequestParam(value="error", required=false) boolean error,
    		Model model) {
		
		
		model.addAttribute("apiKey", new ApiKey());
		model.addAttribute("message", message);
		model.addAttribute("error", error);
		
		// get the api keys for this user
		User user = userService.findUserByEmail(userService.findLoggedInUsername());
		List<ApiKey> apiKeys = apiKeyService.findByUser(user);
		model.addAttribute("apiKeys", apiKeys);
		
        return "apikey_form";
    }
	
	
	
	@PostMapping(path="/add") // Map ONLY POST Requests
	public String registerNewKey (
			@RequestParam String apiKey,
			@RequestParam String description,
			Model model) {
		
		// check if the api key exists
		ApiKey checkApiExists = apiKeyService.findApiKeyByApiKey(apiKey);
		if(checkApiExists != null) {
			// api key already used
			// this is a one in a billion(?) change
			// or someone tries to reuse a key
			// anyway, create a error and send the user back from where they came from
			return "redirect:/api/manageKeys?error=true&message=The API key has already been used.";
		}
		
		// check if the api key is valid
		if(!apiKeyService.checkValidApiKey(apiKey)) {
			// the api key isn't valid
			// redirect with an error
			return "redirect:/api/manageKeys?error=true&message=The API key is not valid.";
		}
		
		
		// get the logged in user
		String loggedInUser = userService.findLoggedInUsername();
		User user = userService.findUserByEmail(loggedInUser);
		
		// create a empty portfolio set
		Set<Portfolio> portfolios = new HashSet<>();
		
		
		// now create the API key object
		ApiKey newApiKey = new ApiKey();
		newApiKey.setApiKey(apiKey);
		newApiKey.setDescription(description);
		newApiKey.setUser(user);
		newApiKey.setPortfolios(portfolios);
		
		// save the object
		apiKeyService.saveApiKey(newApiKey);
		
		
		
		return "redirect:/api/manageKeys?error=false&message=The API key is successfully created.";
	}
	
	@GetMapping(path="/generatekey")
	public @ResponseBody String generateRandomKey() {
		// This returns a JSON or XML with the users
		return apiKeyService.generateRandomKey();
	}
		
		
}
