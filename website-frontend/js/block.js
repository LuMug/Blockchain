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
    .then(json => {
        console.log(json)
    });