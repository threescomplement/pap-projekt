import styles from "./InformationBoxes.module.css"

export interface MessageBoxProps {
    message: string
}

// TODO styling
export default function MessageBox(props: MessageBoxProps) {
    return <div className={props.message === ""
        ? "tw-invisible"
        : "tw-bg-blue-accent tw-text-dark-accent tw-w-1/4 tw-rounded-md tw-text-center tw-pt-4 tw-pb-3 tw-px-2 tw-my-3"}>

        <p>{props.message}</p>
    </div>;
}
