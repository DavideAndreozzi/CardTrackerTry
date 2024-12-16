package com.andreozzi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.andreozzi.entities.Carta;
import com.andreozzi.entities.CartaId;
import com.andreozzi.entities.CustomUserDetails;
import com.andreozzi.entities.Utente;
import com.andreozzi.repos.CartaDAO;
import com.andreozzi.service.CardSearchService;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class CardController {

    @Autowired
    private RestTemplate restTemplate; // Per effettuare richieste HTTP

    @Autowired
    private CartaDAO carteRepository;

    @Autowired
    @Lazy
    private CardSearchService cardSearchService; // Servizio per la ricerca delle carte

    private String AUTH_TOKEN; // Token di autorizzazione per le API
    private Utente loggedUser; // Utente attualmente loggato

    // Recupera l'utente loggato dal contesto di sicurezza
    public Utente getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Utente loggedUser = null;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            loggedUser = ((CustomUserDetails) principal).getUtente();
        }
        return loggedUser;
    }

    @GetMapping("/buildCardLink")
    public String buildCardLink(@RequestParam String cardName, @RequestParam String expansionName) {
        return new CardSearchService().buildCardLink(cardName, expansionName);
    }

    // Elimina una carta specificata dall'utente
    @DeleteMapping("/carte/{idCarta}/{idUtente}")
    @ResponseBody
    public ResponseEntity<String> deleteCarta(@PathVariable("idCarta") int idCarta,
            @PathVariable("idUtente") Long idUtente) {
        CartaId cartaId = new CartaId(idCarta, idUtente); // Crea l'ID della carta
        // Controlla se la carta esiste e la elimina
        if (carteRepository.existsById(cartaId)) {
            carteRepository.deleteById(cartaId);
            return ResponseEntity.ok("Carta eliminata con successo!");
        } else {
            return ResponseEntity.status(404).body("Carta non trovata!");
        }
    }

    // Aggiorna il prezzo di una carta
    @PutMapping("/carte/{idCarta}/{idUtente}")
    @ResponseBody
    public ResponseEntity<String> updatePrezzoCarta(
            @PathVariable("idCarta") int idCarta,
            @PathVariable("idUtente") Long idUtente,
            @RequestParam("prezzo") Double nuovoPrezzo) {

        CartaId cartaId = new CartaId(idCarta, idUtente);
        Optional<Carta> optionalCarta = carteRepository.findById(cartaId);
        // Se la carta esiste, aggiorna il prezzo
        if (optionalCarta.isPresent()) {
            Carta carta = optionalCarta.get();
            carta.setPrezzo_desiderato(nuovoPrezzo);
            carteRepository.save(carta);
            return ResponseEntity.ok("Prezzo aggiornato con successo!");
        } else {
            return ResponseEntity.status(404).body("Carta non trovata!");
        }
    }

    // Salva una nuova carta
    @PostMapping("/salva")
    public ResponseEntity<String> salvaCarta(@ModelAttribute Carta carta) {
        try {
            loggedUser = getLoggedUser(); // Ottieni l'utente loggato
            carta.setUtente(loggedUser); // Imposta l'utente nella carta
            carteRepository.save(carta); // Salva la carta
            return new ResponseEntity<>("{\"message\":\"Carta salvata con successo!\"}", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("{\"message\":\"Errore durante il salvataggio della carta\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cerca blueprint in base all'espansione
    @GetMapping("/searchBlueprintsByExpansion")
    public List<Blueprint> searchBlueprintsByExpansion(@RequestParam int expansionId) {
        if (AUTH_TOKEN == null) {
            loggedUser = getLoggedUser();
            AUTH_TOKEN = loggedUser.getBearer();
        }
        String url = "https://api.cardtrader.com/api/v2/blueprints/export?expansion_id=" + expansionId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + AUTH_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Blueprint[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity,
                Blueprint[].class);
        Blueprint[] response = responseEntity.getBody();

        List<Blueprint> blueprints = new ArrayList<>();

        // Aggiungi i blueprint ottenuti
        if (response != null) {
            for (Blueprint blueprint : response) {
                blueprints.add(blueprint);
            }
        }

        return blueprints; // Restituisci i blueprint trovati
    }

    // Recupera dati dal marketplace
    @GetMapping("/getMarketplaceData")
    public List<MarketplaceProduct> getMarketplaceData(@RequestParam int blueprintId) {

        String url = "https://api.cardtrader.com/api/v2/marketplace/products?blueprint_id=" + blueprintId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJjYXJkdHJhZGVyLXByb2R1Y3Rpb24iLCJzdWIiOiJhcHA6NzkxOCIsImF1ZCI6ImFwcDo3OTE4IiwiZXhwIjo0ODg5NzczMDgwLCJqdGkiOiIxYTQ4NDYzOC1lYzQwLTQyNTQtYjI1Ni1jZmU4MTcxMjljNDIiLCJpYXQiOjE3MzQwOTk0ODAsIm5hbWUiOiJOZW1hMzMgQXBwIDIwMjMxMDE3MDUwNzUzIn0.DR9LeD81J4gQZvmJjwqL49W6IMUH_PgiX15ak924EePq19d9N9Wv8qsMENfXFbITQBIBXRpWqdh977selHJNAmgESvu6CgCPzHsyHpg9No0K73GQoc_n_W2zPsPU5N_terhOf5JnHy2fdV0Q5CuXdRVWJY2VTDBX-94ytzohHFD7Y7GAHQBwwtngatE2XtfkDdt4HPFEvGzy31ENFgvlJ1yatUSamjUbTK__hfviN29Smj7PEZKyFj7x1kgIKCiKoDlqMQnCFKL9wdYeIFsi27m8QhKFkO3SFJuvqo9hKMTyX-9Qf1mmMjkUy-tXOyHlSbU_y4mRCk5V2_ZFQ2I8kg");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<Integer, MarketplaceProduct[]>> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, entity, new ParameterizedTypeReference<Map<Integer, MarketplaceProduct[]>>() {
                });

        Map<Integer, MarketplaceProduct[]> responseMap = responseEntity.getBody();

        List<MarketplaceProduct> products = new ArrayList<>();
        // Aggiungi i prodotti trovati nel marketplace
        if (responseMap != null && responseMap.containsKey(blueprintId)) {
            MarketplaceProduct[] productsArray = responseMap.get(blueprintId);
            if (productsArray != null) {
                for (MarketplaceProduct product : productsArray) {
                    products.add(product);
                }
            }
        }
        return products;
    }

    // Recupera le espansioni disponibili
    @GetMapping("/getExpansions")
    public List<Expansion> getExpansions() {
        if (AUTH_TOKEN == null) {
            loggedUser = getLoggedUser();
            AUTH_TOKEN = loggedUser.getBearer();
        }
        String url = "https://api.cardtrader.com/api/v2/expansions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + AUTH_TOKEN);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Expansion[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity,
                Expansion[].class);
        Expansion[] response = responseEntity.getBody();

        List<Expansion> expansions = new ArrayList<>();
        // Aggiungi le espansioni ottenute
        if (response != null) {
            for (Expansion expansion : response) {
                expansions.add(expansion);
            }
        }

        return expansions; // Restituisci le espansioni trovate
    }

    // Classe Blueprint per mappare i dati delle carte
    public static class Blueprint {
        private int id;
        private String name;

        @JsonProperty("image_url") // Mappa il campo image_url dell'API a imageUrl
        private String imageUrl;

        // Getters e setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    // Classe MarketplaceProduct per rappresentare i prodotti nel marketplace
    public static class MarketplaceProduct {
        private int id;
        private int blueprintId;
        @JsonProperty("name_en")
        private String nameEn;
        private Expansion expansion;
        private int quantity;
        private String description;
        @JsonProperty("properties_hash")
        private PropertiesHash propertiesHash;
        private User user;
        private Price price;

        // Getters e setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getBlueprintId() {
            return blueprintId;
        }

        public void setBlueprintId(int blueprintId) {
            this.blueprintId = blueprintId;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public Expansion getExpansion() {
            return expansion;
        }

        public void setExpansion(Expansion expansion) {
            this.expansion = expansion;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public PropertiesHash getPropertiesHash() {
            return propertiesHash;
        }

        public void setPropertiesHash(PropertiesHash propertiesHash) {
            this.propertiesHash = propertiesHash;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Price getPrice() {
            return price;
        }

        public void setPrice(Price price) {
            this.price = price;
        }
    }

    // Classe Expansion per rappresentare le espansioni
    public static class Expansion {
        @JsonProperty("game_id")
        private String gameId;
        private String code;
        private int id;
        private String name;
        @JsonProperty("name_en")
        private String nameEn;

        // Getters e setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }
    }

    // Classe PropertiesHash per mappare le proprietà delle carte
    public static class PropertiesHash {
        private String condition;
        @JsonProperty("mtg_foil")
        private boolean mtgFoil;
        @JsonProperty("mtg_language")
        private String mtgLanguage;

        // Getters e setters
        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public boolean isMtgFoil() {
            return mtgFoil;
        }

        public void setMtgFoil(boolean mtgFoil) {
            this.mtgFoil = mtgFoil;
        }

        public String getMtgLanguage() {
            return mtgLanguage;
        }

        public void setMtgLanguage(String mtgLanguage) {
            this.mtgLanguage = mtgLanguage;
        }
    }

    // Classe User per rappresentare l'utente nel marketplace
    public static class User {
        private String countryCode;
        private String userType;
        private String username;
        @JsonProperty("can_sell_via_hub")
        private boolean ctZero;

        // Getters e setters
        public boolean isCtZero() {
            return ctZero;
        }

        public void setCtZero(boolean ctZero) {
            this.ctZero = ctZero;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    // Classe Price per rappresentare il prezzo dei prodotti
    public static class Price {
        private int cents;
        private String currency;
        private String formatted;

        // Getters e setters
        public int getCents() {
            return cents;
        }

        public void setCents(int cents) {
            this.cents = cents;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getFormatted() {
            return formatted;
        }

        public void setFormatted(String formatted) {
            this.formatted = formatted;
        }
    }
}
