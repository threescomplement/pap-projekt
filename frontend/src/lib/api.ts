import {authHeader} from "./utils";
import {getStoredUser} from "./User";

const defaultHeaders = {
    'Accept': 'application/json',
    'Content-Type': 'application/json'
};

function myFetch(endpoint: string, headers: any, body: any, method: string, withAuth = true): Promise<any> {
    const user = getStoredUser();
    const auth = (withAuth && user != null) ? authHeader(user) : null;
    const requestBody = (body == null) ? null : JSON.stringify(body);

    return fetch(`${process.env.REACT_APP_API_ROOT}${endpoint}`, {
        method: method,
        headers: {
            ...defaultHeaders,
            ...auth,
            ...headers
        },
        body: requestBody
    });
}

function apiGet(endpoint: string, headers: any = null): Promise<any> {
    return myFetch(endpoint, headers, null, "GET");
}

function apiPost(endpoint: string, body: any, headers: any = null): Promise<any> {
    return myFetch(endpoint, headers, body, "POST");
}

function apiPut(endpoint: string, body: any, headers: any = null): Promise<any> {
    return myFetch(endpoint, headers, body, "PUT");
}

function apiDelete(endpoint: string, body: any, headers: any = null): Promise<any> {
    return myFetch(endpoint, headers, body, "DELETE");
}


const api = {
    get: apiGet,
    post: apiPost,
    put: apiPut,
    delete: apiDelete,
};

export default api;