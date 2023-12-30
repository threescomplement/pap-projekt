import styles from "./ErrorBox.module.css";

export interface ErrorBoxProps {
    message: string
}

export default function ErrorBox(props: ErrorBoxProps) {
    const cls = props.message === ""
        ? styles.hidden
        : styles.errorBox;

    return <div className={cls}>
        <p>{props.message}</p>
    </div>;
}