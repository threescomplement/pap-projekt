import {ChangeEvent, useState} from "react";
import postImport, {ImporterRecord} from "./PostImport";


export default function AdminPanel() {
    const [message, setMessage] = useState<string>("");
    const [fileData, setFileData] = useState<ImporterRecord[]>([]);
    const handleSelectFile = (e: ChangeEvent<HTMLInputElement>) => {
        e.preventDefault();
        const reader = new FileReader();
        reader.onload = async () => {
            const data = JSON.parse(reader.result as string);
            console.log(data)
            setFileData(data)
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
                setMessage(`Import failed with status code ${response.status}`);
            }
        } else {
            setMessage("Nothing to import");
        }

    }

    return <>
        <h1>Admin panel</h1>
        <input type="file" onChange={(e) => handleSelectFile(e)}/>
        <p>{message}</p>
        <button onClick={() => handleClick(fileData)}>
            Import data
        </button>

    </>;
}