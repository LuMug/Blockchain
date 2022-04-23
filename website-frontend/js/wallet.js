"use strict";

let addressContainer = document.getElementById('address');
let utxoContainer = document.getElementById('utxo');

let address = window.location.href.substring(window.location.href.indexOf('?address=') + 9)
addressContainer.innerHTML = address.replaceAll('%2F', '/');

postData('/getUTXO/' + address)
    .then(processJson);

function processJson(json) {
    if (json.status != 'Ok') {
        let container = document.getElementById('info');
        container.innerHTML = "";

        var status = document.createElement('h1');
        var text = document.createTextNode(json.status);
        status.appendChild(text);
        container.appendChild(status);
        return;
    }

    utxoContainer.innerHTML = json.utxo;
}