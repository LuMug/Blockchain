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

async function postData(url = '', data = {}) {
    let href = window.location.href;
    let index = href.indexOf('/', 8);
    href = href.substring(0, index);
    url = href + url;

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

function sendBlocksRequest() {
    postData('/getLatestBlocks/0/10')
        .then(json => {
            var html = "<table><tr><th>Id</th><th>Timestamp</th><th>Hash</th><th>Transactions</th></tr>";

            for (let i = json.blocks.length - 1; i >= 0; i--) {
                html += "<tr>"
                html += "<td align=center>" + json.blocks[i].id + "</td>";
                html += "<td align=center>" + json.blocks[i].timestamp + "</td>";
                html += "<td align=center>" + json.blocks[i].hash + "</td>";
                html += "<td align=center>" + json.blocks[i].nTx + "</td>";
                html += "</tr>"
            }
            html += "</table>";
            document.getElementById("blocksTable").innerHTML = html;
        });
}

/**const table = document.getElementById("blocksTable");
table.innerHTML += "<table>";
for (let i = 0; i < json.blocks.length; i++) {
    table.innerHTML += "<tr>";
    table.innerHTML += "<td>" + json.blocks[i].id + "</td>";
    table.innerHTML += "<td>" + json.blocks[i].timestamp + "</td>";
    table.innerHTML += "<td>" + json.blocks[i].hash + "</td>";
    table.innerHTML += "<td>" + json.blocks[i].nTx + "</td>";
    table.innerHTML += "</tr>";
}
table.innerHTML += "</table>";
//table.innerHTML += json.blocks.length;
console.log(json);*/