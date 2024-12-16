package com.andreozzi.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.andreozzi.entities.Carta;
import com.andreozzi.entities.CartaId;
import com.andreozzi.entities.Utente;

public interface CartaDAO extends JpaRepository<Carta, CartaId> {
	List<Carta> findByIdUtente(Utente userId);
}
