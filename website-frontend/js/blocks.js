"use strict";

const INTERVAL = 1000;

let url = window.location.href.substring(0, window.location.href.indexOf('/', 8));

let container;
let height = 0;

initBlocksUpdate(document.getElementById('blocks'));

function initBlocksUpdate(el) {
    container = el;
    
    update()
    setInterval(update, 30000);
}

function update() {
    let newHeight;
    postData('/getBlockchainHeight')
        .then(blockchain => {
            newHeight = blockchain.height;

            let diff = newHeight - height;

            if (height == 0) {
                diff = Math.min(diff, 10);
                height = newHeight - diff;
            }

            if (diff != 0) {
                let id = height + 1;
                reqBlocks(id, id + diff);
            }


            height += diff;
        });
}

function reqBlocks(id, endId) {
    postData('/getBlock/' + id)
        .then(json => processBlock(json, id))
        .then(() => {
            ++id;
            if (id != endId) {
                reqBlocks(id, endId);
            }
        });
}

function processBlock(json, id) {
    /*
        <div class="bg-info row block">
            <div class="col-1">ID</div>
            <div class="col-3">Timestamp</div>
            <div class="col-7">Hash</div>
            <div class="col-1">nTx</div>
        </div>
    */

    let timestamp = json.timestamp;
    let hash = json.hash;
    let nTx = json.nTx;

    let div = document.createElement('div');
    div.className = 'bg-info row mt-1 block';

    let col1 = document.createElement('div');
    col1.className = 'col-1';
    col1.appendChild(document.createTextNode(id));

    let col2 = document.createElement('div');
    col2.className = 'col-3';
    col2.appendChild(document.createTextNode(new Date(timestamp).toLocaleString()));
    
    let col3 = document.createElement('div');
    col3.className = 'col-7';
    col3.appendChild(document.createTextNode(hash));
    
    let col4 = document.createElement('div');
    col4.className = 'col-1';
    col4.appendChild(document.createTextNode(nTx));

    div.appendChild(col1);
    div.appendChild(col2);
    div.appendChild(col3);
    div.appendChild(col4);

    div.onclick = _ => window.location.href = url + '/block.html?id=' + id;

    container.prepend(div);
}