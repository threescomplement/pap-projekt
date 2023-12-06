import {getStoredUser, User} from "./User";

const defaultHeaders = {
    'Accept': 'application/json',
    'Content-Type': 'application/json'
};

function authHeader(user: User) {
    return {Authorization: `Bearer ${user.token}`}
}

export function buildParamsString(queryParams: any): string {
    let pairs: string[] = [];

    for (const key in queryParams) {
        const value = queryParams[key]
        if (value != null) {
            pairs.push(`${key}=${value}`);
        }
    }

    return "?" + pairs.join("&");
}

function myFetch(endpoint: string, headers: any, body: any, method: string, queryParams: any = null, withAuth = true): Promise<any> {
    const user = getStoredUser();
    const auth = (withAuth && user != null) ? authHeader(user) : null;
    const requestBody = (body == null) ? null : JSON.stringify(body);
    const params = (queryParams != null) ? buildParamsString(queryParams) : ""
    const requestUrl = process.env.REACT_APP_API_ROOT != undefined && endpoint.indexOf(process.env.REACT_APP_API_ROOT) != -1 ?
        endpoint + params
        : process.env.REACT_APP_API_ROOT + endpoint + params;

    return fetch(requestUrl, {
        method: method,
        headers: {
            ...defaultHeaders,
            ...auth,
            ...headers
        },
        body: requestBody
    });
}

function apiGet(endpoint: string, headers: any = null, queryParams: any = null): Promise<any> {
    return myFetch(endpoint, headers, null, "GET", queryParams);
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