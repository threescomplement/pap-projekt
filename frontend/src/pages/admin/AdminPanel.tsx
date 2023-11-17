import {ChangeEvent} from "react";

export default function AdminPanel() {
    const showFile = (e: ChangeEvent<HTMLInputElement>) => {
        e.preventDefault();
        const reader = new FileReader();
        reader.onload = async () => {
            const data = JSON.parse(reader.result as string);
            console.log(data)
        }
        reader.readAsText(e.target.files![0]);
    }

    return <>
        <h1>Admin panel</h1>
        <input type="file" onChange={(e) => showFile(e)}/>
    </>;
}