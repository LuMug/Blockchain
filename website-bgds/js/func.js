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
        mode: 'no-cors',
        headers: {
            'Content-Type': 'application/json',
            "Access-Control-Allow-Origin": "*"
        },
        referrerPolicy: 'no-referrer',
        body: JSON.stringify(data)
    });
    return response.json();
}

function sendBlockRequest() {
    postData('http://127.0.0.1:7777/getLatestBlocks/0/10', {})
        .then(json => {
            console.log(json)
        });
}