"use strict";

const API_URL = 'http://127.0.0.1:6767';

async function postData(url = '', data = {}) {
    url = API_URL + url;
    console.log(url);

    const response = await fetch(url, {
        method: 'POST',
        cache: 'no-cache',
        headers: {
            'Content-Type': 'application/json'
        },
        referrerPolicy: 'no-referrer',
        body: JSON.stringify(data)
    });
    return await response.json();
}
