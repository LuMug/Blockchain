"use strict";

let addressContainer = document.getElementById('address');
let utxoContainer = document.getElementById('utxo');

init();

function init() {
    let index = window.location.href.indexOf('?address=');

    if (index == -1) {
        error('Bad URL');
        return;
    }

    let address = window.location.href.substring(index + 9)
    
    if (address === '') {
        error('Bad URL');
        return;
    }

    addressContainer.innerHTML = address.replaceAll('%2F', '/');
    
    postData('/getUTXO/' + address)
        .then(processJson);
}

function error(msg) {
    let container = document.getElementById('info');
    let title = document.createElement('h1');
    let text = document.createTextNode(msg);
    title.appendChild(text);
    container.innerHTML = '';
    container.appendChild(title);
}

function processJson(json) {
    if (json.status != 'Ok') {
        error(json.status);
        return;
    }

    utxoContainer.innerHTML = json.utxo;
}