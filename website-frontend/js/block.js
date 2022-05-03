"use strict";

let idContainer = document.getElementById('id');
let nonceContainer = document.getElementById('nonce');
let difficultyContainer = document.getElementById('difficulty');
let minerContainer = document.getElementById('miner');
let timestampContainer = document.getElementById('timestamp');
let lastHashContainer = document.getElementById('last-hash');
let hashContainer = document.getElementById('hash');
let nTxContainer = document.getElementById('nTx');

init();

function init() {
    let index = window.location.href.indexOf('?id=');
    
    if (index == -1) {
        error('Bad URL');
        return;
    }

    let id = window.location.href.substring(index + 4);

    if (id === '') {
        error('Bad URL');
        return;
    }

    idContainer.innerHTML = id;

    postData('/getBlock/' + id)
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

    nonceContainer.innerHTML = json.nonce;
    difficultyContainer.innerHTML = json.difficulty;
    minerContainer.innerHTML = '<a href="wallet.html?address=' + json.miner.replaceAll('/', '%2F') + '">' + json.miner + '</a>';
    timestampContainer.innerHTML = new Date(json.timestamp).toLocaleString();
    lastHashContainer.innerHTML = json.last_hash;
    hashContainer.innerHTML = json.hash;
    nTxContainer.innerHTML = json.nTx;
}