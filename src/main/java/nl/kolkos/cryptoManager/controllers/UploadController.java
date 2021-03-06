package nl.kolkos.cryptoManager.controllers;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import nl.kolkos.cryptoManager.services.UploadService;

@Controller
public class UploadController {
	@Autowired
	private UploadService uploadService;
	
	private static String UPLOAD_FOLDER = "/tmp/";
	 
	@RequestMapping("/transaction/import")
	public ModelAndView showUpload() {
		return new ModelAndView("upload_file");
	}
	    
	@PostMapping("/transaction/import")
	public String fileUpload(
			@RequestParam("file") MultipartFile file, 
			@RequestParam("separator") String separator, 
			@RequestParam(name = "containsHeader", defaultValue = "false")  boolean containsHeader, 
			RedirectAttributes redirectAttributes,
			Model model) {

		if (file.isEmpty()) {
			model.addAttribute("message", "Please select a file and try again");
			return "upload_result";
		}
		
		if(!file.getContentType().equals("text/csv")) {
			model.addAttribute("message", "Only *.csv files are allowed");
			return "upload_result";
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
		List<LinkedHashMap<String, String>> results = uploadService.handleFile(UPLOAD_FOLDER + file.getOriginalFilename(), containsHeader, separator);
		
		model.addAttribute("message", "Upload OK!");
		model.addAttribute("results", results);

		return "upload_result";
	}
	
	

	@RequestMapping("/transaction/export")
	public ModelAndView showDownload() {
		return new ModelAndView("download_file");
	}
	
	@PostMapping("/transaction/export")
	public @ResponseBody void downloadFile(@RequestParam("separator") String separator, 
			@RequestParam(name = "containsHeader", defaultValue = "false")  boolean containsHeader, 
			HttpServletResponse response) {
		
		try {
			String filePath = uploadService.exportTransactions(containsHeader, separator);
			
			
			File file = new File(filePath);
	        InputStream in = new FileInputStream(file);

	        response.setContentType("text/csv");
	        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
	        response.setHeader("Content-Length", String.valueOf(file.length()));
	        FileCopyUtils.copy(in, response.getOutputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
