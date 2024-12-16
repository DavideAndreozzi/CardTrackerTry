package com.andreozzi.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.andreozzi.entities.Utente;

import jakarta.servlet.http.HttpSession;

public class SecurityUtils {

    // Metodo per ottenere l'utente corrente dalla sessione
    public static Utente getCurrentUser(HttpSession session) {
        if (session != null) {
            // Recupera l'oggetto utente dalla sessione
            Object userObj = session.getAttribute("user");

            // Controlla se l'oggetto recuperato è effettivamente un'istanza di Utente
            if (userObj instanceof Utente) {
                return (Utente) userObj; 
            }
        }
        return null; // Restituisce null se non trova l'utente
    }

    // Metodo per verificare se l'utente è autenticato
    public static boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
