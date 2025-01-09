package com.andreozzi.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.andreozzi.controllers.CardController.MarketplaceProduct;
import com.andreozzi.controllers.CardController.Price;
import com.andreozzi.entities.Carta;
import com.andreozzi.entities.Utente;
import com.andreozzi.repos.CartaDAO;
import com.andreozzi.repos.UtenteDAO;
import com.andreozzi.service.CardSearchService;
import com.andreozzi.service.EmailService;

import jakarta.mail.MessagingException;

@Component
public class CardScheduler {

    @Autowired
    private CartaDAO cartaDAO;

    @Autowired
    private CardSearchService cardService;

    @Autowired
    private UtenteDAO utenteDAO;

    @Autowired
    private EmailService emailService;

    // Metodo schedulato che viene eseguito ogni "fixedRate" secondi
    @Scheduled(fixedRate = 10000)
    public void tracker() throws MessagingException {
        // Recupera l'utente loggato dalla sessione
        List<Utente> utenti = utenteDAO.findAll();
        for (Utente utente : utenti) {
            String authToken = utente.getBearer();
            String nomeCognome = utente.getNome() + " " + utente.getCognome();
            List<Carta> carte = cartaDAO.findByIdUtente(utente);
            for (Carta carta : carte) {
                // Controlla se la carta non è già stata acquistata (--> implementare quantità)
                if (!carta.getCarta_acquistata()) {
                    double prezzoDesiderato = carta.getPrezzo_desiderato(); // Prezzo desiderato
                    MarketplaceProduct prodotto = cardService.getLowestPrice(carta.getId_carta(),carta.getLanguage(),carta.getCardCondition()); // Ottiene il prodotto più economico in base a lingua e cond
                    Price prezzoRaw = prodotto.getPrice();
                    double prezzo = prezzoRaw.getCents() / 100.0;
                    if (prezzo <= prezzoDesiderato) {

                        cardService.addProductToCart(authToken, prodotto.getId());
                        if (utente.getEmail()!=null){
                        emailService.sendEmail(utente.getEmail(),"Carta nel carrello!",emailService.addedToCartEmail(carta.getNomeCarta()), true);
                        }

                        // Esegue l'acquisto del prodotto
                        cardService.buyProduct(authToken, prodotto.getId(), nomeCognome,
                                utente.getVia(), utente.getZip(), utente.getCitta(),
                                utente.getProvincia(), utente.getPaese(),
                                utente.getNumeroTelefono());

                        // Segna la carta come acquistata
                        carta.setCarta_acquistata(true);
                        cartaDAO.save(carta); // Salva le modifiche della carta
                    }
                }
            }
        }
    }
}