package com.andreozzi.entities;

import java.io.Serializable;
import java.util.Objects;

// Classe chiave composite
public class CartaId implements Serializable {

    private int id_carta;
    private Long idUtente; // Dato che il campo utente è un'entità, usiamo il suo id (Long)

    // Costruttore vuoto (obbligatorio)
    public CartaId() {}

    public CartaId(int id_carta, Long id_utente) {
        this.id_carta = id_carta;
        this.idUtente = id_utente;
    }

    // Getter e setter
    public int getId_carta() {
        return id_carta;
    }

    public void setId_carta(int id_carta) {
        this.id_carta = id_carta;
    }

    public Long getUtente() {
        return idUtente;
    }

    public void setUtente(Long utente) {
        this.idUtente = utente;
    }

    // equals e hashCode (obbligatori per chiave composta)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartaId cartaId = (CartaId) o;
        return Objects.equals(id_carta, cartaId.id_carta) &&
               Objects.equals(idUtente, cartaId.idUtente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_carta, idUtente);
    }
}
