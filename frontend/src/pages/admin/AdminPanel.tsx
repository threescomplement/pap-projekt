import {ChangeEvent, useState} from "react";
import postImport, {ImporterRecord} from "./PostImport";


export default function AdminPanel() {
    const [message, setMessage] = useState<string>("");
    const [fData, setFData] = useState<ImporterRecord[]>([]);
    const onChange = (e: ChangeEvent<HTMLInputElement>) => {
        e.preventDefault();
        const reader = new FileReader();
        reader.onload = async () => {
            const data = JSON.parse(reader.result as string);
            console.log(data)
            setFData(data)
        }
        reader.readAsText(e.target.files![0]);
    }

    async function handleClick(data: ImporterRecord[]) {
        if (data.length > 0) {
            const response = await postImport(data);
            if (response.ok) {
                setMessage("Import successful");
            } else {
                console.error(response)
                setMessage(`Import failed with error code ${response.status}`);
            }
        } else {
            setMessage("Nothing to import");
        }

    }

    return <>
        <h1>Admin panel</h1>
        <input type="file" onChange={(e) => onChange(e)}/>
        <p>{message}</p>
        <button onClick={() => handleClick(fData)}>
            Import data
        </button>

    </>;
}