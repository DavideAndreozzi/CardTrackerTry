// Funzione per ottenere il valore di un parametro di query dall'URL
function getQueryParam(param) {
	const urlParams = new URLSearchParams(window.location.search);
	return urlParams.get(param);
}

const expansionId = getQueryParam('expansionId');
const expansionsListDiv = document.getElementById('expansions-list');
const cardlistTitle = document.getElementById('cardlist-title');
const cardsListDiv = document.getElementById('cards-list');
const cardsDiv = document.getElementById('cards');

// Se c'è un parametro expansionId nella query, mostra le carte per quell'espansione
if (expansionId) {
	fetch(`/searchBlueprintsByExpansion?expansionId=${encodeURIComponent(expansionId)}`)
		.then(response => response.json())
		.then(data => {
			cardsListDiv.style.display = 'block';
			expansionsListDiv.style.display = 'none';
			cardlistTitle.style.display = 'block';

			if (data.length === 0) {
				cardsDiv.innerHTML = '<p>Nessuna carta trovata per questa espansione.</p>';
				return;
			}

			// Mostra tutte le carte trovate con link alla pagina del dettaglio
			data.forEach(blueprint => {
				const blueprintDiv = document.createElement('div');
				blueprintDiv.className = 'card';
				blueprintDiv.innerHTML = `
                            <h3 class="cardName">${blueprint.name}</h3>
                            <p class='cardId'>ID: ${blueprint.id}</p>
                            <a href="/cards?blueprint_id=${blueprint.id}&image_url=${encodeURIComponent(blueprint.image_url)}">
                                <img class="cardImg" src="${blueprint.image_url}" alt="${blueprint.name}" style="max-width: 200px; cursor: pointer;">
                            </a>
                        `;
				cardsDiv.appendChild(blueprintDiv);
			});
		})
		.catch(error => {
			console.error('Errore:', error);
			cardsDiv.innerHTML = '<p>Errore durante il recupero delle carte.</p>';
		});
} else {
	// Recupera l'elenco delle espansioni
	fetch('/getExpansions')
		.then(response => response.json())
		.then(expansions => {
			cardsListDiv.style.display = 'none'; // Nascondi la lista delle carte se non è presente un expansionId
			cardlistTitle.style.display = 'none'; // Nascondi il titolo delle carte

			if (expansions && expansions.length > 0) {
				expansions.forEach(expansion => {
					if (expansion.game_id == 1) {
						const expansionDiv = document.createElement('div');
						expansionDiv.className='expansion-name';
						expansionDiv.innerHTML = `
                                <a class="expansion-link" href="?expansionId=${expansion.id}"><h3>${expansion.name}</h3></a>
                            `;
						expansionsListDiv.appendChild(expansionDiv);
					}
				});
			} else {
				expansionsListDiv.innerHTML = '<p>Nessuna espansione trovata.</p>';
			}
		})
		.catch(error => {
			console.error('Errore:', error);
			expansionsListDiv.innerHTML = '<p>Errore durante il recupero delle espansioni.</p>';
		});
}

// Funzione per filtrare espansioni e carte in base all'input dell'utente
function filterItems() {
    const query = document.getElementById('search-bar').value.toLowerCase();
    
    const expansions = document.querySelectorAll('#expansions-list .expansion-name');
    const cards = document.querySelectorAll('#cards-list .card');
    
    let noResult = document.getElementById('noResult');
    if (!noResult) {
        noResult = document.createElement('div');
        noResult.id = 'noResult';
        noResult.style.textAlign = 'center';
        noResult.style.padding = '20px';
        noResult.style.display = 'none'; 
        noResult.textContent = 'Nessun risultato trovato';
        document.body.appendChild(noResult); 
    }

    let expansionFound = false;
    expansions.forEach(expansion => {
        const expansionName = expansion.innerText.toLowerCase();
        if (expansionName.includes(query)) {
            expansion.style.display = 'block';
            expansionFound = true;
        } else {
            expansion.style.display = 'none';
        }
    });

    let cardFound = false;
    cards.forEach(card => {
        const cardName = card.querySelector('.cardName').innerText.toLowerCase();
        const cardId = card.querySelector('.cardId').innerText.toLowerCase();
        if (cardName.includes(query) || cardId.includes(query)) {
            card.style.display = 'inline-block';
            cardFound = true;
        } else {
            card.style.display = 'none';
        }
    });

    if (!expansionFound && !cardFound) {
        noResult.style.display = 'block';
    } else {
        noResult.style.display = 'none';
    }
}

async function searchCard() {
    const input = document.getElementById("searchInput").value;

    if (!input.trim()) {
        alert("Please enter a card name!");
        return;
    }

    try {
        const response = await fetch(`/search?name=${encodeURIComponent(input)}`);
        if (response.ok) {
            const blueprintId = await response.text(); // Ottieni l'ID del blueprint
            window.location.href = `/cards?blueprint_id=${blueprintId}`; // Reindirizza
        } else {
            alert("Card not found! Please try again.");
        }
    } catch (error) {
        console.error("Error while searching for the card:", error);
        alert("An error occurred. Please try again later.");
    }
}

let timeout = null; // Per il debounce delle richieste

async function fetchSuggestions() {
    const input = document.getElementById("searchInput").value;
    const suggestionsDiv = document.getElementById("suggestions");

    if (!input.trim()) {
        suggestionsDiv.innerHTML = ""; // Pulisci i suggerimenti se non c'è input
        return;
    }

    // Evita di inviare troppe richieste
    clearTimeout(timeout);
    timeout = setTimeout(async () => {
        try {
            const response = await fetch(`/suggestions?query=${encodeURIComponent(input)}`);
            if (response.ok) {
                const suggestions = await response.json();
                suggestionsDiv.innerHTML = suggestions
                    .map(suggestion => 
                        `<div onclick="selectSuggestion(${suggestion.blueprintId})">
                            ${suggestion.name}
                        </div>`
                    )
                    .join("");
            } else {
                suggestionsDiv.innerHTML = "<div>No results found</div>";
            }
        } catch (error) {
            console.error("Error fetching suggestions:", error);
        }
    }, 300); // Aspetta 300ms prima di inviare la richiesta
}

function selectSuggestion(blueprintId) {
    // Reindirizza alla pagina delle carte con il blueprintId come parametro
    window.location.href = `/cards?blueprint_id=${blueprintId}`;
}
