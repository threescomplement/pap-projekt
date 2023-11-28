import api from "../../lib/api";

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
    return api.post("/admin/importer/data", payload);
}