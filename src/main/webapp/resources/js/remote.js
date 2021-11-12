import {AlarmClass, AlarmRegistration, EffectiveRegistration} from "./entities.js";

class Remote extends EventTarget {
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
        return fetch('proxy/rest/locations')
    }

    getCategories() {
        return fetch('proxy/rest/categories');
    }

    getPriorities() {
        return fetch('proxy/rest/priorities')
    }
}

const remote = new Remote();

const worker = new Worker('worker.js', {"type": "module"});

worker.onmessage = function(e) {
    let event;

    switch(e.data) {
        case "class":
             event = new CustomEvent("class", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "class-highwatermark":
            event = new CustomEvent("class-highwatermark", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "registration":
            event = new CustomEvent("registration", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "registration-highwatermark":
            event = new CustomEvent("registration-highwatermark", { detail: null });
            remote.dispatchEvent(event);
            break;
        default:
            console.log('Unknown worker message: ', e.data);
    }
}

export default remote;