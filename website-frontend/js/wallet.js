"use strict";

let addressContainer = document.getElementById('address');
let utxoContainer = document.getElementById('utxo');

let url = window.location.href.substring(0, window.location.href.indexOf('/', 8));

init();

var address;

function init() {
    let index = window.location.href.indexOf('?address=');

    if (index == -1) {
        error('Bad URL');
        return;
    }

    address = window.location.href.substring(index + 9)
    
    if (address === '') {
        error('Bad URL');
        return;
    }

    addressContainer.innerHTML = address.replaceAll('%2F', '/');
    
    postData('/getUTXO/' + address)
        .then(processUTXOJson);

    postData('/getTxs/' + address)
        .then(processTxsJson);
}

function error(msg) {
    let container = document.getElementById('info');
    let title = document.createElement('h1');
    let text = document.createTextNode(msg);
    title.appendChild(text);
    container.innerHTML = '';
    container.appendChild(title);
}

function processUTXOJson(json) {
    if (json.status != 'Ok') {
        error(json.status);
        return;
    }

    utxoContainer.innerHTML = json.utxo;
}

function processTxsJson(json) {
    if (json.status != 'Ok') {
        return;
    }
    /*
        <div class="bg-info row block">
        <div class="col-4">From</div>
        <div class="col-4">To</div>
        <div class="col-1">Amount</div>
        <div class="col-3">Timestamp</div>
        </div>
    */

    console.log(json);
    let container = document.getElementById('txs');

    for (let i = 0; i < json.txs.length; i++) {
        let sender = json.txs[i].sender;
        let recipient = json.txs[i].recipient;
        let amount = json.txs[i].amount;
        let timestmap = json.txs[i].timestamp;

        let spent = address == json.txs[i].sender;
        
        let div = document.createElement('div');
        div.className = 'bg-info row mt-1 tx';
        
        let col1 = document.createElement('div');
        col1.className = 'col-4';
        col1.appendChild(document.createTextNode(sender));
        
        let col2 = document.createElement('div');
        col2.className = 'col-4';
        col2.appendChild(document.createTextNode(recipient));
        
        let col3 = document.createElement('div');
        let amountElement = document.createElement('span');
        amountElement.appendChild(document.createTextNode(amount));
        col3.className = 'col-1';
        col3.appendChild(amountElement);
        amountElement.style.color = spent ? 'red' : 'green';
        
        let col4 = document.createElement('div');
        col4.className = 'col-3';
        col4.appendChild(document.createTextNode(new Date(timestmap).toLocaleString()));
        
        div.appendChild(col1);
        div.appendChild(col2);
        div.appendChild(col3);
        div.appendChild(col4);
        
        div.onclick = _ => window.location.href = url + '/transaction.html?hash=' + json.txs[i].hash;
        
        container.prepend(div);
    }
}