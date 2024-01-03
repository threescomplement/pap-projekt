export interface MessageBoxProps {
    message: string
}

// TODO styling
export default function MessageBox(props: MessageBoxProps) {
    return <div>
        <p>{props.message}</p>
    </div>;
}