const meta = document.querySelector('meta');
const contextPath = meta && meta.dataset.contextPath || '';
const appVersion = meta && meta.dataset.appVersion || '';

class Remote extends EventTarget {
    constructor() {
        super();

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
            worker = new Worker(contextPath + '/worker-' + appVersion + '.mjs', {"type": "module"});
        } else {
            worker = new Worker(contextPath + '/worker-' + appVersion + '.js');
        }

        worker.onmessage = function(e) {
            remote.dispatchEvent(new CustomEvent(e.data, { detail: null }));
        }

        this.clear = function() {
            worker.postMessage("clear");
        }
    }

    setEntity(formData, path) {
        /*Treat empty string as no-field*/
        for (var pair of Array.from(formData.entries())) {
            if (pair[1] === "") {
                formData.delete(pair[0]);
            }
        }

        return fetch(contextPath + path, {
            method: "PUT",
            body: new URLSearchParams(formData),
            headers: {
                Accept: "application/json",
                "Content-type": 'application/x-www-form-urlencoded;charset=UTF-8'
            }
        });
    }

    setInstance(formData) {
        return this.setEntity(formData, "/proxy/rest/instance");
    }

    setClass(formData) {
        return this.setEntity(formData, "/proxy/rest/class");
    }

    getPriorities() {
        return fetch(contextPath + '/proxy/rest/priorities')
    }
}

const remote = new Remote();

export default remote;