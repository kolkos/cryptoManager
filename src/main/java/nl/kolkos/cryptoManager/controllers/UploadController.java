package nl.kolkos.cryptoManager.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import nl.kolkos.cryptoManager.services.UploadService;

@Controller
public class UploadController {
	@Autowired
	private UploadService uploadService;
	
	private static String UPLOAD_FOLDER = "/tmp/";
	 
	@RequestMapping("/upload")
	public ModelAndView showUpload() {
		return new ModelAndView("upload_file");
	}
    
	@PostMapping("/upload")
	public ModelAndView fileUpload(
			@RequestParam("file") MultipartFile file, 
			@RequestParam("separator") String separator, 
			@RequestParam("containsHeader") boolean containsHeader, 
			RedirectAttributes redirectAttributes) {

		if (file.isEmpty()) {
			return new ModelAndView("upload_result", "message", "Please select a file and try again");
		}
		
		if(!file.getContentType().equals("text/csv")) {
			return new ModelAndView("upload_result", "message", "Only *.csv files are allowed");
		}
		
		try {
			// read and write the file to the selected location-
			byte[] bytes = file.getBytes();
			Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
			Files.write(path, bytes);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// now it is time to process the file
		

		return new ModelAndView("upload_result", "message", "File Uploaded sucessfully");
	}
	
}
