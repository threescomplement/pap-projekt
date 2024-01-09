import {ChangeEvent, useState} from "react";
import postImport, {ImporterRecord} from "./PostImport";
import UserManagementPanel from "./UserManagementPanel";
import "../../ui/index.css"
import classes from "./AdminPanel.module.css"


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
                setMessage("Import udany");
            } else {
                console.error(response)
                setMessage(`Import nieudany (${response.status})`);
            }
        } else {
            setMessage("Wybierz plik z danymi");
        }

    }

    return <div className={"container" /*fixme?*/}>
        <h1 className={classes.adminHeader}>Panel administratora</h1>
        <input type="file" onChange={(e) => handleSelectFile(e)}/>
        <p>{message}</p>
        <button onClick={() => handleClick(fileData)}>
            Importuj dane
        </button>

        <UserManagementPanel/>
    </div>;
}