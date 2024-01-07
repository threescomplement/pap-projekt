import {MdDeleteForever, MdEdit} from "react-icons/md";
import React, {MouseEventHandler, useState} from "react";
import {ConfirmationPopup} from './ConfirmationPopup';


interface EditBarProps {
    handleDelete: MouseEventHandler
    deleteConfirmationQuery: string
    handleEdit: MouseEventHandler<HTMLButtonElement>
    canEdit: boolean
}


export function EditBar({handleDelete, deleteConfirmationQuery, handleEdit, canEdit}: EditBarProps) {
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
    const whenDeleteConfirmation = <ConfirmationPopup
        query={deleteConfirmationQuery}
        handleConfirmation={handleDelete}
        setVisibility={setShowDeleteConfirmation}
    />

    const regularContent = <div>
        {canEdit ? <button onClick={event => handleEdit(event)}><MdEdit/></button> : null}
        <button
            onClick={(event) => {
                event.stopPropagation();
                setShowDeleteConfirmation(true);
            }}
        ><MdDeleteForever/>
        </button>
    </div>


    return showDeleteConfirmation ? whenDeleteConfirmation : regularContent;
}