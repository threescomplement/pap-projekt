import React from 'react';
import styles from './ConfirmationPopup.module.css';

export interface ConfirmationPopupProps {
    query: string,
    handleConfirmation: () => void
}

export function ConfirmationPopup({query, handleConfirmation}: ConfirmationPopupProps) {
    const [visible, setVisible] = React.useState(true);

    const handleConfirmationClick = () => {
        handleConfirmation();
        setVisible(false);
    }

    const handleCancelClick = () => {
        setVisible(false);
    }

    if (!visible) {
        return null;
    }

    return (
        <div className={styles.confirmationPopup}>
            <p>{query}</p>
            <div className={styles.buttonContainer}>
                <button className={styles.confirmButton} onClick={handleConfirmationClick}>Yes</button>
                <button className={styles.cancelButton} onClick={handleCancelClick}>No</button>
            </div>
        </div>
    );
}