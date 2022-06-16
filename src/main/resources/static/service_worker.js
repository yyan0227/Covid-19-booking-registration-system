
// https://stackoverflow.com/questions/43813770/how-to-intercept-all-http-requests-including-form-submits   (1st answer)

function fetchWithParamAddedToRequestHeader(request) {
    const response = serialize(request)
    .then(serialized => {return deserialize(serialized)})
    .then(request => {return fetch(request)})
    return response;
}

function serialize(request) {
    var headers = {};
    for (var entry of request.headers.entries()) {
        headers[entry[0]] = entry[1];
    }
    headers["Authorization"] = jwt_service;     // set Authorization field in request header to JWT
    var serialized = {
        url: request.url,
        headers: headers,
        method: request.method,
        mode: 'same-origin',    // https://stackoverflow.com/questions/35420980/how-to-alter-the-headers-of-a-request (1st answer)
        credentials: request.credentials,
        cache: request.cache,
        redirect: 'manual',      // https://stackoverflow.com/questions/35420980/how-to-alter-the-headers-of-a-request (1st answer)
        referrer: request.referrer
        // referrer: '/login'
    };

    if (request.method !== 'GET' && request.method !== 'HEAD') {
        return request.clone().text().then(function(body) {
            serialized.body = body;
            return Promise.resolve(serialized);
        });
    }

    return Promise.resolve(serialized);
}

function deserialize(data) {
    return Promise.resolve(new Request(data.url, data));
}


let jwt_service;
self.onmessage = function handleMessageFromMain(msg) {
    jwt_service = msg["data"]["jwt"];
};

self.addEventListener('fetch', function(event) {
    if (event.request.url.includes("localhost")) {
        // don't intercept Http requests made to URLs other than http://localhost
        event.respondWith(
            fetchWithParamAddedToRequestHeader(event.request)
        );
    }
});

