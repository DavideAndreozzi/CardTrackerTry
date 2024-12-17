//funzione per eliminare la carta salvata
function deleteCarta(idCarta, idUtente) {
	fetch(`/carte/${idCarta}/${idUtente}`, { 
		method: 'DELETE'
	}).then(response => {
		if (response.ok) {
			alert("Carta eliminata con successo!");
			window.location.reload();
		} else {
			response.text().then(text => alert(text));
		}
	}).catch(error => {
		console.error('Errore:', error);
	});
}

//funzione per visualizzare i dati della carta nella pagina 
function showUpdateModal(idCarta, nomeCarta, idUtente,  prezzoAttuale) {
	document.getElementById("idUtente").value = idUtente;
	document.getElementById("idCarta").value = idCarta;
	document.getElementById("nomeCarta").textContent = nomeCarta;
	document.getElementById("prezzoAttuale").textContent = prezzoAttuale;
	document.getElementById("nuovoPrezzo").value = prezzoAttuale; 
	const modal = new bootstrap.Modal(document.getElementById("updateModal"));
	document.getElementById("updateModal").style.display = "block";

	modal.show();
}

//funzione per aggiornare il prezzo della carta 
function updatePrezzo() {
	const idCarta = document.getElementById("idCarta").value;
	const idUtente = document.getElementById("idUtente").value;
	const nuovoPrezzo = document.getElementById("nuovoPrezzo").value;
	//API per aggiornare il prezzo della carta
	fetch(`/carte/${idCarta}/${idUtente}?prezzo=${nuovoPrezzo}`, {
		method: 'PUT'
	}).then(response => {
		if (response.ok) {
			alert("Prezzo aggiornato con successo!");
			window.location.reload();
			document.getElementById("updateModal").style.display = "none";
		} else {
			response.text().then(text => alert(text));
		}
	}).catch(error => {
		console.error('Errore:', error);
	});
	
}