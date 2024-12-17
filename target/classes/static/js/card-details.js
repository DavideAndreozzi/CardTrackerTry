// Funzione per ottenere il valore di un parametro di query dall'URL
function getQueryParam(param) {
	const urlParams = new URLSearchParams(window.location.search);
	return urlParams.get(param);
}

const blueprintId = getQueryParam('blueprint_id');
const imageUrl = getQueryParam('image_url');
document.getElementById('blueprintId').value = blueprintId;

if (blueprintId) {
	console.log(`Richiesta dei dati per la carta con ID: ${blueprintId}`);

	// Recupera i dati della carta dal controller Spring
	fetch(`/getMarketplaceData?blueprintId=${blueprintId}`)
		.then(response => {
			if (!response.ok) {
				throw new Error(`Errore HTTP: ${response.status}`);
			}
			return response.json();
		})
		.then(data => {
			console.log('Dati ricevuti:', data);

			const cardInfoDiv = document.getElementById('card-info');
			if (data && data.length > 0) {
				// Mostra la carta iniziale
				let selectedCard = data.find(card => card.user && card.user.can_sell_via_hub);
				renderCard(selectedCard, imageUrl, cardInfoDiv);
			} else {
				cardInfoDiv.innerHTML = '<p>Nessun dato disponibile per questa carta.</p>';
			}
		})
		.catch(error => {
			console.error('Errore:', error);
			document.getElementById('card-info').innerHTML = `<p>Errore durante il recupero dei dati della carta: ${error.message}</p>`;
		});
} else {
	document.getElementById('card-info').innerHTML = '<p>ID carta non trovato.</p>';
}

// Funzione per aggiornare la visualizzazione della carta
function renderCard(cardData, imageUrl, cardInfoDiv) {
	const condition = cardData.properties_hash ? cardData.properties_hash.condition : 'N/A';
	const foil = cardData.properties_hash ? cardData.properties_hash.mtg_foil : 'N/A';
	document.getElementById('name_en').value = cardData.name_en;
	// Chiamata all'endpoint Java per generare il link
	fetch(`/buildCardLink?cardName=${encodeURIComponent(cardData.name_en)}&expansionName=${encodeURIComponent(cardData.expansion.name_en)}`)
		.then(response => {
			if (!response.ok) {
				throw new Error(`Errore HTTP: ${response.status}`);
			}
			return response.text(); // Il metodo restituisce una stringa
		})
		.then(cardLink => {
			// Mostra i dettagli della carta e il link
			cardInfoDiv.innerHTML = `
				<h2 id="card-title">${cardData.name_en}</h2>
				<img id="card-img" src="${imageUrl}" alt="${cardData.name_en}">
				<div id="card-description">
					<p>Carta ID: ${cardData.id}</p>
					<p>Espansione: ${cardData.expansion.name_en}</p>
					<p>Prezzo più basso: ${cardData.price.formatted}</p>
					<p>Condizione: ${condition}</p>
					<p>Foil: ${foil}</p>
					<p><a href="${cardLink}" target="_blank">Guarda su CardTrader</a></p>
				</div>
			`;
		})
		.catch(error => {
			console.error('Errore nel generare il link della carta:', error);
			cardInfoDiv.innerHTML = `<p>Errore nel generare il link della carta: ${error.message}</p>`;
		});
}


document.getElementById('salvaCartaForm').addEventListener('submit', function (event) {
	event.preventDefault(); // Prevenire il comportamento di default del form

	// Creare un oggetto FormData per ottenere i dati del form
	const formData = new FormData(this);

	// Effettuare una richiesta POST usando Fetch API
	fetch('/salva', {
		method: 'POST',
		body: formData,
	})
		.then(response => {
			if (!response.ok) {
				throw new Error(`Errore HTTP: ${response.status}`);
			}
			return response.json();
		})
		.then(data => {
			// Mostra un avviso con il messaggio dal server
			alert(data.message);
		})
		.catch(error => {
			alert("Si è verificato un errore durante il salvataggio della carta.");
			console.error("Errore:", error);
		});
});
