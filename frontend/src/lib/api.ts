function myFetch(endpoint: string, headers: any, body: any, method: string = "GET"): Promise<any> {
    return fetch(`${process.env.REACT_APP_API_ROOT}${endpoint}`, {
        method: method,
        headers: headers,
        body: JSON.stringify(body)
    });
}

export default myFetch;