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
    postData('http://127.0.0.1/getLatestBlocks/0/10', {})
        .then(json => {
            console.log(json)
        });
}