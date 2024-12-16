package com.andreozzi.entities;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final Utente utente;

    public CustomUserDetails(Utente utente) {
        this.utente = utente;
    }

    public Utente getUtente() {
        return utente;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Gestisci i ruoli e le autorizzazioni qui se necessario
        return null; 
    }
    
    public Long getIdUtente() {
    	return utente.getId();
    }
    
    public String getBearer() {
    	return utente.getBearer();
    }
    
    @Override
    public String getPassword() {
        return utente.getPassword();
    }

    @Override
    public String getUsername() {
        return utente.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Gestisci la logica se l'account è scaduto
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Gestisci la logica se l'account è bloccato
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Gestisci la logica se le credenziali sono scadute
    }

    @Override
    public boolean isEnabled() {
        return true; // Gestisci la logica se l'account è abilitato
    }
}
