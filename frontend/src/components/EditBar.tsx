import {MdDeleteForever, MdEdit} from "react-icons/md";
import React, {MouseEventHandler} from "react";


interface EditBarProps {
    handleDelete: React.MouseEventHandler<HTMLButtonElement>
}

export function EditBar({handleDelete}: EditBarProps) {
    return <div>
        <button><MdEdit/></button>
        <button onClick={(event) => {
            handleDelete(event);
        }}><MdDeleteForever/></button>
    </div>
}