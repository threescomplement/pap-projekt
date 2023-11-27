import {defaultHeaders} from "../../lib/utils";

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

export default function postImport(payload: ImporterRecord[]) {
    return fetch(`${process.env.REACT_APP_API_ROOT}importer/data`, {
        method: "POST",
        headers: defaultHeaders,
        body: JSON.stringify(payload),
    });
}