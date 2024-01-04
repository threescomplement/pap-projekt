import styles from "./InformationBoxes.module.css"

export interface MessageBoxProps {
    message: string
}

export default function MessageBox(props: MessageBoxProps) {
    const cls = props.message === ""
        ? styles.hidden
        : styles.messageBox
    return <div className={cls}>
        <p>{props.message}</p>
    </div>;
}