import {AlarmClass, AlarmRegistration, EffectiveRegistration} from "./entities.js";

const worker = new Worker('worker.js', {"type": "module"});

worker.onmessage = function(e) {
    console.log('remote onmessage', e.data);
}

class Remote {
    start() {
        console.log('starting remote!');
    }

    setRegistration(formData) {
        /*Treat empty string as no-field*/
        for (var pair of Array.from(formData.entries())) {
            if (pair[1] === "") {
                formData.delete(pair[0]);
            }
        }

        return fetch("proxy/rest/registered", {
            method: "PUT",
            body: new URLSearchParams(formData),
            headers: {
                Accept: "application/json",
                "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
            }
        });
    }

    setClass(formData) {
        /*Treat empty string as no-field*/
        for (var pair of Array.from(formData.entries())) {
            if (pair[1] === "") {
                formData.delete(pair[0]);
            }
        }

        return fetch("proxy/rest/class", {
            method: "PUT",
            body: new URLSearchParams(formData),
            headers: {
                Accept: "application/json",
                "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
            }
        });
    }

    getLocations() {
        fetch('proxy/rest/locations')
    }

    getCategories() {
        return fetch('proxy/rest/categories');
    }

    getPriorities() {
        fetch('proxy/rest/priorities')
    }
}

const remote = new Remote();

export default remote;