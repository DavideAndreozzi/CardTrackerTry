package com.andreozzi.service;

import org.springframework.stereotype.Service;
import com.andreozzi.entities.Utente;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserSessionService {

	private final ConcurrentHashMap<String, Utente> userSessions = new ConcurrentHashMap<>();

	// Variabile statica per memorizzare un unico utente loggato.
	private static Utente utenteLoggato;

	// Metodo statico per impostare l'utente loggato.
	public static void setUtenteLoggato(Utente utente) {
		utenteLoggato = utente;
	}

	// Metodo statico per ottenere l'utente loggato.
	// Restituisce l'utente corrente memorizzato nella variabile statica.
	public static Utente getUtenteLoggato() {
		return utenteLoggato;
	}

	// Aggiunge un utente alla mappa di sessioni usando il nome utente come chiave.
	public void addUser(String username, Utente utente) {
		userSessions.put(username, utente);
	}

	// Restituisce l'utente loggato corrispondente al nome utente fornito.
	// Cerca l'utente nella mappa delle sessioni.
	public Utente getLoggedUser(String username) {
		return userSessions.get(username);
	}

	// Rimuove l'utente dalla mappa di sessioni quando effettua il logout o la
	// sessione scade.
	public void removeUser(String username) {
		userSessions.remove(username);
	}
}
