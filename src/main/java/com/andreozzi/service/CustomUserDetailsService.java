package com.andreozzi.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.andreozzi.entities.Utente;
import com.andreozzi.entities.CustomUserDetails;
import com.andreozzi.repos.UtenteDAO;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtenteDAO utenteDAO;

    public CustomUserDetailsService(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente utente = utenteDAO.findByUsername(username);

        return new CustomUserDetails(utente);
    }
}
