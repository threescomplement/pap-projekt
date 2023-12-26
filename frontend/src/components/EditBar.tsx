import {MdDeleteForever, MdEdit} from "react-icons/md";
import React, {MouseEventHandler} from "react";


interface EditBarProps {
    handleDelete: MouseEventHandler<HTMLButtonElement>
    handleEdit: MouseEventHandler<HTMLButtonElement>
    canEdit: boolean
}

export function EditBar({handleDelete, handleEdit, canEdit}: EditBarProps) {
    return <div>
        {canEdit ? <button onClick={event => handleEdit(event)}><MdEdit/></button> : null}
        <button onClick={(event) => {handleDelete(event)}}><MdDeleteForever/></button>
    </div>
}