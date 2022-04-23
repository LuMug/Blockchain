"use strict";

let idContainer = document.getElementById('id');
let nonceContainer = document.getElementById('nonce');
let minerContainer = document.getElementById('miner');
let timestampContainer = document.getElementById('timestamp');
let lastHashContainer = document.getElementById('last-hash');
let hashContainer = document.getElementById('hash');
let nTxContainer = document.getElementById('nTx');

let id = window.location.href.substring(window.location.href.indexOf('?id=') + 4)
idContainer.innerHTML = id;

postData('/getBlock/' + id)
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

    nonceContainer.innerHTML = json.nonce;
    minerContainer.innerHTML = '<a href="wallet.html?address=' + json.miner.replaceAll('/', '%2F') + '">' + json.miner + '</a>';
    timestampContainer.innerHTML = new Date(json.timestamp * 1000);
    lastHashContainer.innerHTML = json.last_hash;
    hashContainer.innerHTML = json.hash;
    nTxContainer.innerHTML = json.nTx;
}