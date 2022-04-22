"use strict";

const port = 6767;

function redirect(name) {
    document.getElementById('frame').src = name;
}

function uploadFiles() {
    var files = document.getElementById('file_upload').files;
    if (files.length == 0) {
        alert("Please first choose or drop any file(s)...");
        return;
    }
    var filenames = "";
    for (var i = 0; i < files.length; i++) {
        filenames += files[i].name + "\n";
    }
    alert("Selected file(s) :\n____________________\n" + filenames);
}

// ----------------------------------

async function postData(url = '', data = {}) {
    let href = window.location.href;
    let index = href.indexOf('/', 8);
    href = href.substring(0, index); // compute once
    url = href + ":" + port + url; /// port

    const response = await fetch(url, {
        method: 'POST',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/json'
        },
        referrerPolicy: 'no-referrer',
        body: JSON.stringify(data)
    });
    return response.json();
}
/*

function sendSizeRequest() {
    postData('/getBlockchainSize')
        .then(json => {
            var html = "<h2>Dimensione blockchain: ";
            html += json.size;
            html += "</h2";
            document.getElementById("statistiche").innerHTML = html;
            console.log(json);
        });
}

function sendTransactionsRequest() {
    postData('/getLatestTransactions/0/10')
        .then(json => {
            var html = "<table><tr><th>From</th><th>To</th><th>Amount</th><th>Timestamp</th></tr>";
            for (let i = json.transactions.length - 1; i >= 0; i--) {
                html += "<tr>"
                html += "<td align=center>" + json.transactions[i].from + "</td>";
                html += "<td align=center>" + json.transactions[i].to + "</td>";
                html += "<td align=center>" + json.transactions[i].amount + "</td>";
                html += "<td align=center>" + json.transactions[i].timestamp + "</td>";
                html += "</tr>"
            }
            html += "</table>";
            document.getElementById("transactionsTable").innerHTML = html;
        });
}
*/
