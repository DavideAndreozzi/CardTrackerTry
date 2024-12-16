package com.andreozzi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.andreozzi.entities.Carta;
import com.andreozzi.entities.CustomUserDetails;
import com.andreozzi.entities.Utente;
import com.andreozzi.repos.CartaDAO;
import com.andreozzi.repos.UtenteDAO;
import com.andreozzi.service.UserSessionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

	@Autowired
	private UtenteDAO utenteDAO;

	@Autowired
	private CartaDAO cartaDAO;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Mostra il modulo di login
	@GetMapping("/login")
	public String showLoginForm() {
		UserSessionService.setUtenteLoggato(null); // Resetta l'utente loggato
		return "login";
	}

	// Mostra il modulo di registrazione
	@GetMapping("/register")
	public String showRegistrationForm() {
		return "register";
	}

	// Registra un nuovo utente
	@PostMapping("/register")
	public String registerUser(@ModelAttribute Utente utente) {

		if (utenteDAO.findByUsername(utente.getUsername()) != null) {
			return "/register";
		}
		System.out.println(utente.getBearer());
		// Codifica la password e salva l'utente
		utente.setPassword(passwordEncoder.encode(utente.getPassword()));
		utenteDAO.save(utente);
		return "/login";
	}

	// Mostra la pagina principale (home)
	@GetMapping("/index")
	public String home(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Utente utenteLoggato = null;

		// Recupera l'utente autenticato
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();
			utenteLoggato = ((CustomUserDetails) principal).getUtente();
			UserSessionService.setUtenteLoggato(utenteLoggato); // Imposta l'utente loggato nella sessione
		}
		return "index";
	}

	// Mostra la pagina delle espansioni
	@GetMapping("/expansions")
	public String expansions(Model model) {
		return "expansions";
	}

	// Mostra le carte tracciate dall'utente
	@GetMapping("/trackedCards")
	public String trackedCards(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Utente utente = null;

		// Recupera l'utente autenticato
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();
			utente = ((CustomUserDetails) principal).getUtente();
		}

		// Recupera le carte associate all'utente
		List<Carta> carte = cartaDAO.findByIdUtente(utente);
		model.addAttribute("carte", carte); // Aggiunge le carte al modello
		return "savedCards";
	}

	// Mostra la pagina dei dettagli della carta
	@GetMapping("/cards")
	public String card(Model model) {
		return "card-details";
	}
}
