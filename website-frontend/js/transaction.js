"use strict";

let blockIdContainer = document.getElementById('blockId');
let senderContainer = document.getElementById('sender');
let recipientContainer = document.getElementById('recipient');
let amounContainert = document.getElementById('amount');
let timestampContainer = document.getElementById('timestamp');
let lastTxHashContainer = document.getElementById('lastTxHash');
let signatureContainer = document.getElementById('signature');
let hashContainer = document.getElementById('hash');

init();

function init() {
    let index = window.location.href.indexOf('?hash=');

    if (index == -1) {
        error('Bad URL');
        return;
    }
    
    let hash = window.location.href.substring(index + 6)
    
    if (hash === '') {
        error('Bad URL')
        return;
    }

    if (hash == 'AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=') {
        error('This hash means that this is the first transaction');
        return;
    }

    hashContainer.innerHTML = hash.replaceAll('%2F', '/');
        
    postData('/getTx/' + hash)
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

    blockIdContainer.innerHTML = '<a href="block.html?id=' + json.blockId + '">' + json.blockId + '</a>';
    senderContainer.innerHTML = '<a href="wallet.html?address=' + json.sender.replaceAll('/', '%2F') + '">' + json.sender + '</a>';
    recipientContainer.innerHTML = '<a href="wallet.html?address=' + json.recipient.replaceAll('/', '%2F') + '">' + json.recipient + '</a>';
    amounContainert.innerHTML = json.amount;
    timestampContainer.innerHTML = new Date(json.timestamp).toLocaleString();
    lastTxHashContainer.innerHTML = '<a href="transaction.html?hash=' + json.lastTxHash.replaceAll('/', '%2F') + '">' + json.lastTxHash + '</a>';
    signatureContainer.innerHTML = json.signature;
}