package com.andreozzi.repos;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andreozzi.entities.Utente;


public interface UtenteDAO extends JpaRepository<Utente, Long> {
	Utente findByUsername(String username);
}
