import React, {MouseEventHandler} from 'react';
import styles from '../ui/components/ConfirmationPopup.module.css';

export interface ConfirmationPopupProps {
    query: string,
    handleConfirmation:MouseEventHandler
    setVisibility: Function
}

export function ConfirmationPopup({query, handleConfirmation, setVisibility}: ConfirmationPopupProps) {

    const handleCancelClick = () => {
        setVisibility(false);
    }

    return (
        <div className={styles.confirmationPopup}>
            <p>{query}</p>
            <div className={styles.buttonContainer}>
                <button className={styles.confirmButton} onClick={e=> {
                    handleConfirmation(e);
                    setVisibility(false);
                }}>Tak</button>
                <button className={styles.cancelButton} onClick={handleCancelClick}>Nie</button>
            </div>
        </div>
    );
}