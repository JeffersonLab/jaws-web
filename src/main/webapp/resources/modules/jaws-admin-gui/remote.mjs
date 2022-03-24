const meta = document.querySelector('meta');
const contextPath = meta && meta.dataset.contextPath || '';

class Remote extends EventTarget {
    start() {

    }

    setInstance(formData) {
        /*Treat empty string as no-field*/
        for (var pair of Array.from(formData.entries())) {
            if (pair[1] === "") {
                formData.delete(pair[0]);
            }
        }

        return fetch(contextPath + "/proxy/rest/instance", {
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

        return fetch(contextPath + "/proxy/rest/class", {
            method: "PUT",
            body: new URLSearchParams(formData),
            headers: {
                Accept: "application/json",
                "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
            }
        });
    }

    getLocations() {
        return fetch(contextPath + '/proxy/rest/locations')
    }

    getCategories() {
        return fetch(contextPath + '/proxy/rest/categories');
    }

    getPriorities() {
        return fetch(contextPath + '/proxy/rest/priorities')
    }
}

const remote = new Remote();

function supportsWorkerType() {
    let supports = false;
    const tester = {
        get type() { supports = true; } // it's been called, it's supported
    };
    try {
        // We use "blob://" as url to avoid a useless network request.
        // This will either throw in Chrome
        // either fire an error event in Firefox
        // which is perfect since
        // we don't need the worker to actually start,
        // checking for the type of the script is done before trying to load it.
        const worker = new Worker('blob://', tester);
    } finally {
        return supports;
    }
}

let worker;

if( supportsWorkerType() ) {
    worker = new Worker(contextPath + '/worker.mjs', {"type": "module"});
} else {
    worker = new Worker(contextPath + '/worker.js');
}

worker.onmessage = function(e) {
    let event;

    switch(e.data) {
        case "category":
            event = new CustomEvent("category", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "category-highwatermark":
            event = new CustomEvent("category-highwatermark", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "class":
             event = new CustomEvent("class", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "class-highwatermark":
            event = new CustomEvent("class-highwatermark", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "instance":
            event = new CustomEvent("instance", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "instance-highwatermark":
            event = new CustomEvent("instance-highwatermark", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "location":
            event = new CustomEvent("location", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "location-highwatermark":
            event = new CustomEvent("location-highwatermark", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "effective":
            event = new CustomEvent("effective", { detail: null });
            remote.dispatchEvent(event);
            break;
        case "effective-highwatermark":
            event = new CustomEvent("effective-highwatermark", { detail: null });
            remote.dispatchEvent(event);
            break;
        default:
            console.log('Unknown worker message: ', e.data);
    }
}

export default remote;