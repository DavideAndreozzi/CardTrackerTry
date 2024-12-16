package com.andreozzi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.andreozzi.controllers.CardController;
import com.andreozzi.controllers.CardController.MarketplaceProduct;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CardSearchService {

	@Autowired
	private CardController cardController; 

	@Autowired
	private RestTemplate restTemplate; 

	// Ottiene il prezzo più basso per un determinato blueprintId
	public MarketplaceProduct getLowestPrice(int blueprintId, String desiredLanguage, String desiredCondition) {
    // Recupera i dati del marketplace per il blueprintId
    List<MarketplaceProduct> products = cardController.getMarketplaceData(blueprintId);

    // Filtra i prodotti che soddisfano le condizioni richieste: user.ctZero(), mtgLanguage e condition
    Optional<MarketplaceProduct> prodottop = products.stream()
            .filter(prodotto -> prodotto.getUser().isCtZero() && 
                               prodotto.getPropertiesHash().getMtgLanguage().equalsIgnoreCase(desiredLanguage) && 
                               prodotto.getPropertiesHash().getCondition().equalsIgnoreCase(desiredCondition))
            .findFirst(); // Prende il primo prodotto che soddisfa le condizioni

    // Controlla se esiste un prodotto che soddisfa le condizioni, altrimenti lancia un'eccezione
    if (prodottop.isEmpty()) {
        throw new NoSuchElementException("Nessun prodotto trovato con i criteri specificati.");
    }

    // Recupera il prodotto filtrato
    MarketplaceProduct prodotto = prodottop.get();

    return prodotto;
}


	// Ottiene l'ID del prodotto per un dato blueprintId
	public int getId(int blueprintId) {
		List<MarketplaceProduct> products = cardController.getMarketplaceData(blueprintId);
		Optional<MarketplaceProduct> prodottop = products.stream().filter(prodotto -> prodotto.getUser().isCtZero())
				.findFirst(); // Prende il primo prodotto trovato


		MarketplaceProduct prodotto = prodottop.get(); 
		int id = prodotto.getId(); // Ottiene l'ID del prodotto
		return id;
	}

	// Aggiunge un prodotto al carrello
	public void addProductToCart(String authToken, int productId) {
		String url = "https://api.cardtrader.com/api/v2/cart/add";

		// Crea intestazioni
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + authToken);
		headers.set("Content-Type", "application/json");

		// Crea corpo della richiesta
		String jsonInput = String.format("{\"product_id\": %d, \"quantity\": 1, \"via_cardtrader_zero\": true}",
				productId);
		HttpEntity<String> request = new HttpEntity<>(jsonInput, headers);

		// Invia la richiesta POST
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		System.out.println(response.getBody()); 
	}

	// Effettua l'acquisto di un prodotto
	public void buyProduct(String authToken, int productId, String name, String street, String zip, String city,
			String province, String country, String phone) {
		String url = "https://api.cardtrader.com/api/v2/cart/purchase";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + authToken);

		// Crea corpo della richiesta
		String jsonInput = String.format(
				"{\"billing_address\": {" + "    \"name\": \"%s\"," + "    \"street\": \"%s\"," + "    \"zip\": \"%s\","
						+ "    \"city\": \"%s\"," + "    \"state_or_province\": \"%s\","
						+ "    \"country_code\": \"%s\"," + "    \"phone\": \"%s\"" + "}," + " \"shipping_address\": {"
						+ "    \"name\": \"%s\"," + "    \"street\": \"%s\"," + "    \"zip\": \"%s\","
						+ "    \"city\": \"%s\"," + "    \"state_or_province\": \"%s\","
						+ "    \"country_code\": \"%s\"," + "    \"phone\": \"%s\"" + "  }}",
				name, street, zip, city, province, country, phone, name, street, zip, city, province, country, phone);

		HttpEntity<String> request = new HttpEntity<>(jsonInput, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
	}

	public String buildCardLink(String cardName, String expansionName){
		String baseUrl = "https://www.cardtrader.com/cards/";
         	String cleanedCardName = cardName.replaceAll("[^a-zA-Z0-9\\s]", "");
         	String cleanedExpansionName = expansionName.replaceAll("[^a-zA-Z0-9\\s]", "");
         	String newCardName = cleanedCardName.toLowerCase().replace(" ", "-");
         	String newExpansionName = cleanedExpansionName.toLowerCase().replace(" ", "-");
        	String fullUrl = baseUrl + newCardName + "-" + newExpansionName;
		return fullUrl;
	}
}
