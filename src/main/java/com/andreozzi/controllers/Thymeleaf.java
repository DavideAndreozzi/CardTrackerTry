package com.andreozzi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Thymeleaf {

	 @GetMapping("/home")
	    public String home(Model model) {
	    	return "index";
	    }
	    
	    // @GetMapping("/cards")
	    // public String card(Model model) {
	    // 	return "card-details";
	    // }
}
