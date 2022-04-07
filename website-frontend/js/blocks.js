"use strict";

const INTERVAL = 1000;

let url = window.location.href.substring(0, window.location.href.indexOf('/', 8));

let container;

function initBlocksUpdate(el) {
    container = el;
    update();
    update();
    update();
    update();
    update();
    update();
    update();
    //setInterval(update, 1000);
}

function update() {
    postData('/getLatestBlocks/0/10')
        .then(json => {
            for (let i = 0; i < json.blocks.length; i++) {
                /*
                    <div class="bg-info row block">
                        <div class="col-1">ID</div>
                        <div class="col-3">Timestamp</div>
                        <div class="col-7">Hash</div>
                        <div class="col-1">nTx</div>
                    </div>
                */

                // il bordino rotondo dei blocchi non si vede

                let id = json.blocks[i].id;
                let timestamp = json.blocks[i].timestamp;
                let hash = json.blocks[i].hash;
                let nTx = json.blocks[i].nTx;

                let div = document.createElement('div');
                div.className = 'bg-info row mt-1 block';

                let col1 = document.createElement('div');
                col1.className = 'col-1';
                col1.appendChild(document.createTextNode(id));

                let col2 = document.createElement('div');
                col2.className = 'col-3';
                col2.appendChild(document.createTextNode(timestamp));
                
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

                div.onclick = _ => window.location.href = url + '/block=' + hash;

                container.prepend(div);
            }
        
        });
}