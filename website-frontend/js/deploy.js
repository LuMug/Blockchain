"use strict";

Dropzone.autoDiscover = false;
const dropzone = new Dropzone("#dropzone", { url: API_URL + '/deploy' });