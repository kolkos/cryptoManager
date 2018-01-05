package nl.kolkos.cryptoManager.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
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

import nl.kolkos.cryptoManager.Mail;
import nl.kolkos.cryptoManager.PasswordResetToken;
import nl.kolkos.cryptoManager.Portfolio;
import nl.kolkos.cryptoManager.Role;
import nl.kolkos.cryptoManager.User;
import nl.kolkos.cryptoManager.repositories.PortfolioRepository;
import nl.kolkos.cryptoManager.repositories.RoleRepository;
import nl.kolkos.cryptoManager.services.MailService;
import nl.kolkos.cryptoManager.services.PasswordResetTokenService;
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
	
	@Autowired
	private PasswordResetTokenService passwordResetTokenService;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private MailService mailService;

	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@GetMapping("/menu")
	public String depositForm(Model model) {
		model.addAttribute("username", userService.findLoggedInUsername());

		return "menu";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user",
					"There is already a user registered with the email provided");
		}
		if (!user.getPassword().equals(user.getRepeatPassword())) {
			bindingResult.rejectValue("password", "error.user", "The passwords are not equal");
			bindingResult.rejectValue("repeatPassword", "error.user", "The passwords are not equal");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			userService.saveUser(user);
			modelAndView.addObject("successMessage",
					"<strong>Registration successfull!<strong>. You can now <a href='/login'>login</a>.");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");

		}
		return modelAndView;
	}

	@RequestMapping(value = "/profile/edit", method = RequestMethod.GET)
	public ModelAndView editProfile() {
		ModelAndView modelAndView = new ModelAndView();
		User user = userService.findUserByEmail(userService.findLoggedInUsername());
		modelAndView.addObject("user", user);
		modelAndView.setViewName("edit_profile");
		return modelAndView;
	}

	@RequestMapping(value = "/profile/edit", method = RequestMethod.POST)
	public String updateUserProfile(@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "lastName", required = true) String lastName,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "repeatPassword", required = false) String repeatPassword, Model model) {

		// get the current logged in user
		User user = userService.findUserByEmail(userService.findLoggedInUsername());
		// user.setEmail(userService.findLoggedInUsername());

		// change (or don't) the name and last name
		user.setName(name);
		user.setLastName(lastName);

		boolean passwordChanged = false;

		// check if the password is filled
		if (password != null && ! password.equals("")) {
			//  check if the passwords are equal
			if (!password.equals(repeatPassword)) {
				model.addAttribute("error", "<strong>Error!</strong> Passwords are not equal");
			} else {
				System.out.println("password changed");
				
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

	@RequestMapping(value = "/install", method = RequestMethod.GET)
	public @ResponseBody String initialInstall() {
		// check if the ADMIN role exists
		Role testAdminRole = roleRepository.findByRole("ADMIN");
		if (testAdminRole != null) {
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
		Set<User> users1 = new HashSet<>();
		users1.add(admin);
		portfolio1.setUsers(users1);

		Portfolio portfolio2 = new Portfolio();
		portfolio2.setName("Portfolio for Regular User");
		portfolio2.setDescription("The regular user has access to this portfolio");
		Set<User> users2 = new HashSet<>();
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

	@RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
	public ModelAndView forgotPasswordPage() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("forgot_password_form");
		return modelAndView;
	}
	
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
	public String handleForgotPassword(@RequestParam(value = "email", required = true) String email,
			Model model, HttpServletRequest request) {
		
		// check if the emailaddress existsmy
		User user = userService.findUserByEmail(email);
		if(user == null) {
			model.addAttribute("error", "Unknown email addresss");
			return "forgot_password_form";
		}
		
		// register the token
		String token = passwordResetTokenService.createPasswordResetToken(user);
		
		// now create a new email
		Mail mail = new Mail();
		mail.setFrom(env.getProperty("custom.mail.send.from"));
		mail.setTo(user.getEmail());
		mail.setSubject("Password reset request");
		
		Map<String, Object> modelMail = new HashMap<>();
		modelMail.put("token", token);
		modelMail.put("user", user);
		
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        modelMail.put("signature", url);
        modelMail.put("resetUrl", url + "/resetPassword?token=" + token);
        mail.setModel(modelMail);
        
        mailService.sendEmail(mail);
		
		model.addAttribute("success", "An email with the reset instructions have been send");
		
		return "forgot_password_form";
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public String displayResetPasswordPage(@RequestParam(required = false) String token,
                                           Model model) {

        PasswordResetToken resetToken = passwordResetTokenService.findByToken(token);
        if (resetToken == null){
            model.addAttribute("error", "Could not find password reset token.");
        } else if (resetToken.isExpired()){
            model.addAttribute("error", "Token has expired, please request a new password reset.");
        } else {
            model.addAttribute("token", resetToken.getToken());
        }

        return "reset_password_form";
    }
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public String handleResetPasswordPage(
    		@RequestParam(required = true) String token,
    		@RequestParam(required = true) String password,
    		@RequestParam(required = true) String confirmPassword,
    		Model model) {

        PasswordResetToken resetToken = passwordResetTokenService.findByToken(token);
        if (resetToken == null){
            model.addAttribute("error", "Could not find password reset token.");
            return "reset_password_form";
        }
        
        // check if the two passwords are equal
        if(! password.equals(confirmPassword)) {
	        	model.addAttribute("error", "Passwords are not equal.");
	        	return "reset_password_form";
        }
        
        // get the user attached to the user
        User user = resetToken.getUser();
        user.setPassword(confirmPassword);
        user.setRepeatPassword(confirmPassword);
        
        // update the user
        userService.saveUser(user);
        
        model.addAttribute("success", "Password changed! You can now login with the new password.");

        return "reset_password_form";
    }

}
