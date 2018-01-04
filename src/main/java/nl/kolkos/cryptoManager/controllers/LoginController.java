package nl.kolkos.cryptoManager.controllers;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Role;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.RoleRepository;
import nl.kolkos.cryptoManager.services.UserService;



@Controller
public class LoginController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	@Qualifier(value = "portfolioRepository")
	private PortfolioRepository portfolioRepository;

	@RequestMapping(value={"/login"}, method = RequestMethod.GET)
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	@GetMapping("/menu")
    public String depositForm(Model model) {
        model.addAttribute("username", userService.findLoggedInUsername());
        
        
        return "menu";
    }
	
	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public ModelAndView registration(){
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}
	
	@RequestMapping(value="/profile/edit", method = RequestMethod.GET)
	public ModelAndView editProfile(){
		ModelAndView modelAndView = new ModelAndView();
		User user = userService.findUserByEmail(userService.findLoggedInUsername());
		modelAndView.addObject("user", user);
		modelAndView.setViewName("edit_profile");
		return modelAndView;
	}
	
	@RequestMapping(value="/profile/edit", method = RequestMethod.POST)
	public String updateUserProfile(
			@RequestParam(value="name", required=true) String name,
			@RequestParam(value="lastName", required=true) String lastName,
			@RequestParam(value="password", required=false) String password,
			@RequestParam(value="repeatPassword", required=false) String repeatPassword,
			Model model) {
		
		// get the current logged in user
		User user = userService.findUserByEmail(userService.findLoggedInUsername());
		//user.setEmail(userService.findLoggedInUsername());
		
		// change (or don't) the name and last name
		user.setName(name);
		user.setLastName(lastName);
		
		boolean passwordChanged = false;
		
		// check if the password is filled
		if(password != null) {
			// check if the passwords are equal
			if(!password.equals(repeatPassword)) {
				model.addAttribute("error", "<strong>Error!</strong> Passwords are not equal");
			}else {
				// set the password
				user.setPassword(repeatPassword);
				user.setRepeatPassword(repeatPassword);
				passwordChanged = true;
			}
		}
		
		userService.updateUser(user, passwordChanged);
		model.addAttribute("success", "<strong>Success!<strong>. Profile updated.");
		model.addAttribute("user", user);
		
		return "edit_profile";
	}
	
	@RequestMapping(value="/install", method = RequestMethod.GET)
	public @ResponseBody String initialInstall(){
		// check if the ADMIN role exists
		Role testAdminRole = roleRepository.findByRole("ADMIN");
		if(testAdminRole != null) {
			return "The initial installation has already been done...";
		}
		
		String usernameAdministrator = "admin@localhost";
		String passwordAdministrator = "admin";
		
		String usernameUser = "user@localhost";
		String passwordUser = "user";
		
		// create roles
		Role userRole = new Role();
		userRole.setRole("USER");
		
		// create roles
		Role adminRole = new Role();
		adminRole.setRole("ADMIN");
		
		roleRepository.save(userRole);
		roleRepository.save(adminRole);
		    	
		// create a two users
		User admin = new User();
		admin.setEmail(usernameAdministrator);
		admin.setLastName("Administrator");
		admin.setName("Administrator");
		admin.setPassword(passwordAdministrator);
		
		User user = new User();
		user.setEmail(usernameUser);
		user.setLastName("User");
		user.setName("User");
		user.setPassword(passwordUser);
		
		// save the users
		userService.saveUser(admin);
		userService.saveUser(user);
		
		// create portfolios
		Portfolio portfolio1 = new Portfolio();
		portfolio1.setName("Portfolio for Administrator");
		portfolio1.setDescription("The administrator user has access to this portfolio");
		Set <User> users1 = new HashSet<>();
		users1.add(admin);
		portfolio1.setUsers(users1);
		
		Portfolio portfolio2 = new Portfolio();
		portfolio2.setName("Portfolio for Regular User");
		portfolio2.setDescription("The regular user has access to this portfolio");
		Set <User> users2 = new HashSet<>();
		users2.add(user);
		portfolio2.setUsers(users2);
		
		// save the portfolios
		portfolioRepository.save(portfolio1);
		portfolioRepository.save(portfolio2);
		
		String returnMessage = "<h1>Done!</h1>";
		returnMessage += "<p>The following users are created:</p>";
		returnMessage += "<p>Administrator:<br/>";
		returnMessage += "Username: " + usernameAdministrator + "<br/>";
		returnMessage += "Password: " + passwordAdministrator + "</p>";
		returnMessage += "<p>Normal user:<br/>";
		returnMessage += "Username: " + usernameUser + "<br/>";
		returnMessage += "Password: " + passwordUser + "</p>";
		
		
		
		return returnMessage;
	}
	
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult
					.rejectValue("email", "error.user",
							"There is already a user registered with the email provided");
		}
		if(!user.getPassword().equals(user.getRepeatPassword())) {
			bindingResult
					.rejectValue("password", "error.user",
						"The passwords are not equal");
			bindingResult
					.rejectValue("repeatPassword", "error.user",
						"The passwords are not equal");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "<strong>Registration successfull!<strong>. You can now <a href='/login'>login</a>.");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");
			
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/admin/home", method = RequestMethod.GET)
	public ModelAndView home(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
	
	
}
