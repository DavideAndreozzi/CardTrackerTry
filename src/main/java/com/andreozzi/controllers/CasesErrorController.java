package com.andreozzi.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CasesErrorController implements ErrorController {
	// in fase di elaborazione.....
	@GetMapping("/error")
	public String handleError(jakarta.servlet.http.HttpServletRequest request, Model model) {
		Object status = request.getAttribute(jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE);
		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());

			// Gestisci gli errori specifici, come 404
			if (statusCode == 404) {
				model.addAttribute("message", "La pagina che stai cercando non esiste.");
				return "404"; 
			} else if (statusCode == 500) {
				model.addAttribute("message", "Errore interno del server.");
				return "500"; 
			}
		}

		// Per altri errori o non specificati
		model.addAttribute("message", "Si è verificato un errore.");
		return "error"; 
	}

	public String getErrorPath() {
		return "/error";
	}
}
