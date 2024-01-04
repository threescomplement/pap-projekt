import {MdDeleteForever, MdEdit} from "react-icons/md";
import React, {MouseEventHandler, useState} from "react";
import {ConfirmationPopup} from './ConfirmationPopup';


interface EditBarProps {
    handleDelete: MouseEventHandler
}


export function EditBar({handleDelete}: EditBarProps) {
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
    const whenDeleteConfirmation = <ConfirmationPopup
        query={"Czy na pewno chcesz usunąć opinię?"}
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