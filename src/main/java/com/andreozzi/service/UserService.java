package com.andreozzi.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.andreozzi.entities.Utente;
import com.andreozzi.repos.UtenteDAO;

@Service
public class UserService {
	
	@Autowired
    private UtenteDAO userRepository;
    
	@Autowired
    private PasswordEncoder passwordEncoder;


    public Utente registerUser(Utente utente) {
        // Verifica se l'utente esiste già
        if (userRepository.findByUsername(utente.getUsername()) != null) {
            throw new RuntimeException("Username already taken");
        }
        System.out.println(utente.getNome());
        // Crea un nuovo utente con password crittografata
        utente.setPassword(passwordEncoder.encode(utente.getPassword()));

        // Salva l'utente nel database
        return userRepository.save(utente);
    }
}
