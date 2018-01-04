package nl.kolkos.cryptoManager.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    public String apiKeyForm(Model model) {
				
		model.addAttribute("apiKey", new ApiKey());
		
		// get the api keys for this user
		User user = userService.findUserByEmail(userService.findLoggedInUsername());
		List<ApiKey> apiKeys = apiKeyService.findByUser(user);
		model.addAttribute("apiKeys", apiKeys);
		
        return "apikey_form";
    }
	
	
	
	@PostMapping(path="/manageKeys") // Map ONLY POST Requests
	public String registerNewKey (
			@RequestParam String apiKey,
			@RequestParam String description,
			Model model) {
		
		model.addAttribute("apiKey", new ApiKey());
		// get the api keys for this user
		User user = userService.findUserByEmail(userService.findLoggedInUsername());
		List<ApiKey> apiKeys = apiKeyService.findByUser(user);
		model.addAttribute("apiKeys", apiKeys);
		
		
		// check if the api key exists
		ApiKey checkApiExists = apiKeyService.findApiKeyByApiKey(apiKey);
		if(checkApiExists != null) {
			// api key already used
			// this is a one in a billion(?) change
			// or someone tries to reuse a key
			// anyway, create a error and send the user back from where they came from
			
			model.addAttribute("error", "API Key already exists.");
			return "apikey_form";
		}
		
		// check if the api key is valid
		if(!apiKeyService.checkValidApiKey(apiKey)) {
			// the api key isn't valid
			// redirect with an error
			model.addAttribute("error", "API Key is not valid.");
			return "apikey_form";
		}
		
		
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
		
		// refresh api keys
		apiKeys = apiKeyService.findByUser(user);
		model.addAttribute("apiKeys", apiKeys);
		
		model.addAttribute("success", "API Key successfully added.");
		return "apikey_form";
	}
	
	@GetMapping(path="/generatekey")
	public @ResponseBody String generateRandomKey() {
		// This returns a JSON or XML with the users
		return apiKeyService.generateRandomKey();
	}
		
	@RequestMapping(value = "/details/{apiKeyId}", method = RequestMethod.GET)
	public String showDepositDetails(@PathVariable("apiKeyId") long apiKeyId, Model model) {
		
		// check if the selected key is owned by the current user
		ApiKey apiKey = apiKeyService.findById(apiKeyId);
		User user = userService.findUserByEmail(userService.findLoggedInUsername());
		if(!apiKey.getUser().equals(user)) {
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "API Key");
			return "not_authorized";
		}
		
		// add the ApiKey to the model
		model.addAttribute("apiKey", apiKey);
		
		
		return "apikey_details";
	}
	
	@PostMapping(path="/deleteKey") // Map ONLY POST Requests
	public String removeApiKey (
			@RequestParam long apiKeyId,
			Model model) {
		// check if the selected key is owned by the current user
		ApiKey apiKey = apiKeyService.findById(apiKeyId);
		User user = userService.findUserByEmail(userService.findLoggedInUsername());
		if(!apiKey.getUser().equals(user)) {
			model.addAttribute("firstName", user.getName());
			model.addAttribute("object", "API Key");
			return "not_authorized";
		}
		
		apiKeyService.removeApiKey(apiKey);
		
		
		return "redirect:/api/manageKeys";
	}
	
}
