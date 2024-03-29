import {Review} from "../lib/Review";
import {ReviewComment} from "../lib/ReviewComment";
import {useState} from "react";
import {GoReport} from "react-icons/go";
import ReportService, {reasons} from "../lib/Reports";
import MessageBox from "./MessageBox";
import ErrorBox from "./ErrorBox";
import styles from "../ui/components/ReportBox.module.css"

export interface ReportBoxProps {
    reportedEntity: Review | ReviewComment
}
export default function ReportBox(props: ReportBoxProps) {
    const [displayFull, setDisplayFull] = useState(false);
    /*todo: not set an initial value for the reason this way*/
    const [reason, setReason] = useState("nieprawdziwe informacje");
    const [reportSent, setReportSent] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    if (!displayFull) {
        return <button onClick={() => setDisplayFull(true)}><GoReport /></button>;
    }

    if (reportSent) {
        return <MessageBox message={"Dziękujemy, twoje zgłoszenie zostanie rozpatrzone"}/>
    }

    async function handleReport() {
        try {
            const ok = await ReportService.reportEntity(props.reportedEntity, reason);
            if (ok) {
                setReportSent(true);
            } else {
                setErrorMessage("Operacja nie powiodła się");
            }
        } catch (e) {
            console.error(e);
            setErrorMessage("Operacja nie powiodła się");
        }
    }

    return <div className={styles.reportBox}>
        <h3>Wybierz powód zgłoszenia</h3>
        <ErrorBox message={errorMessage}/>
        <select onChange={e => setReason(e.target.value)}>
            {reasons.map(r => <option value={r}>{r}</option>)}
        </select>
        <div className={styles.buttonContainer}>
            <button onClick={handleReport}>Zgłoś</button>
        <button onClick={()=>setDisplayFull(false)}>Anuluj</button>
        </div>
    </div>
}