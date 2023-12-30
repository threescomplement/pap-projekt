export interface MessageBoxProps {
    message: string
}

export default function MessageBox(props: MessageBoxProps) {
    return <div>
        <p>{props.message}</p>
    </div>;
}