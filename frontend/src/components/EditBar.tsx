import {MdDeleteForever, MdEdit} from "react-icons/md";
import React, {MouseEventHandler, useState} from "react";
import {ConfirmationPopup} from './ConfirmationPopup';


interface EditBarProps {
    handleDelete: MouseEventHandler
    deleteConfirmationQuery: string
}


export function EditBar({handleDelete, deleteConfirmationQuery}: EditBarProps) {
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
    const whenDeleteConfirmation = <ConfirmationPopup
        query={deleteConfirmationQuery}
        handleConfirmation={handleDelete}
        setVisibility={setShowDeleteConfirmation}
    />

    const regularContent = <div>
        <button><MdEdit/></button>
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