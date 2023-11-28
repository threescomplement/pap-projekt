import {defaultHeaders, authHeader} from "../../lib/utils";
import {User} from "../../lib/User";

export interface ImporterRecord {
    usos_code: string,
    symbol: string,
    language: string,
    level: string,
    module: string,
    type: string,
    title: string,
    teacher: string,
    tim: string
}

export default function postImport(payload: ImporterRecord[], user: User) {
    return fetch(`${process.env.REACT_APP_API_ROOT}admin/importer/data`, {
        method: "POST",
        headers: {
            ...authHeader(user),
            ...defaultHeaders
        },
        body: JSON.stringify(payload),
    });
}