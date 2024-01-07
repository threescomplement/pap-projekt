import {useState} from "react";

export interface EditableTextProps {
    text: string,
    setText: (text: string) => void
}

export default function EditableText({text, setText}: EditableTextProps) {
    const [editing, setEditing] = useState(false);

    const toggleEditing = () => setEditing(!editing);


    if (!editing) {
        return <p onClick={toggleEditing}>{text}</p>
    } else {
        return <input type="text" value={text} onChange={(e) => setText(e.target.value)}
        onBlur={toggleEditing}/>
    }
}