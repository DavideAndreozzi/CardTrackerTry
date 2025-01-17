package com.andreozzi.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@IdClass(CartaId.class)
@Table(name = "carte")
public class Carta {

	@Id
	private int id_carta;
	
	@Id
	@ManyToOne
    @JoinColumn(name = "ID_UTENTE", nullable = false)
    private Utente idUtente;
	
	private Double prezzo_desiderato;
	private Boolean carta_acquistata = false;
	@Column(name="nome_carta")
	private String nomeCarta;
	@Column(name="card_condition")
	private String cardCondition;
	private String language;
	private boolean foil;

	public boolean isFoil() {
		return this.foil;
	}

	public boolean getFoil() {
		return this.foil;
	}

	public void setFoil(boolean foil) {
		this.foil = foil;
	}
	
	

	public Utente getIdUtente() {
		return this.idUtente;
	}

	public void setIdUtente(Utente idUtente) {
		this.idUtente = idUtente;
	}

	public Boolean isCarta_acquistata() {
		return this.carta_acquistata;
	}

	public String getCardCondition() {
		return this.cardCondition;
	}
	
	public void setCardCondition(String cardCondition) {
		this.cardCondition = cardCondition;
	}
	
	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getNomeCarta() {
		return nomeCarta;
	}
	public void setNomeCarta(String nomeCarta) {
		this.nomeCarta = nomeCarta;
	}
	public int getId_carta() {
		return id_carta;
	}
	public void setId_carta(int id_carta) {
		this.id_carta = id_carta;
	}
	public Double getPrezzo_desiderato() {
		return prezzo_desiderato;
	}
	public void setPrezzo_desiderato(Double prezzo_desiderato) {
		this.prezzo_desiderato = prezzo_desiderato;
	}
	public Boolean getCarta_acquistata() {
		return carta_acquistata;
	}
	public void setCarta_acquistata(Boolean carta_acquistata) {
		this.carta_acquistata = carta_acquistata;
	}
	public Utente getUtente() {
		return idUtente;
	}
	public void setUtente(Utente utente) {
		this.idUtente = utente;
	}
	
	
	
	
}
