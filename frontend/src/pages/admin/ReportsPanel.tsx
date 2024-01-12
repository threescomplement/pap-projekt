import ReportService, {Report} from "../../lib/Reports";
import {useEffect, useState} from "react";
import ErrorBox from "../../components/ErrorBox";
import {Link} from "react-router-dom";
import {ConfirmationPopup} from "../../components/ConfirmationPopup";
import styles from "../../ui/pages/AdminPanel.module.css"

// TODO styling
export default function ReportsPanel() {
    const [reports, setReports] = useState<Report[]>([]);
    const [errorMessage, setErrorMessage] = useState("");

    async function loadReports() {
        try {
            const reports = await ReportService.getAllReports();
            setReports(reports);
            setErrorMessage("");
        } catch (e) {
            setErrorMessage("Pobieranie nie powiodło się");
        }
    }

    async function handleContentOk(report: Report) {
        try {
            const ok = await ReportService.deleteReport(report);
            if (ok) {
                await loadReports();
                setErrorMessage("");
            } else {
                setErrorMessage("Nie udało się usunąć zgłoszenia");
            }
        } catch (e) {
            console.error(e);
            setErrorMessage("Operacja nie powiodła się");
        }
    }

    async function handleDeleteContent(report: Report) {
        try {
            const ok = await ReportService.deleteReportedEntity(report);
            if (ok) {
                await loadReports();
                setErrorMessage("");
            } else {
                setErrorMessage("Nie udało się usunąć zgłoszonej treści");
            }
        } catch (e) {
            console.error(e);
            setErrorMessage("Operacja nie powiodła się");
        }
    }

    useEffect(() => {
        loadReports()
            .catch(e => console.error(e));
    }, [])

    return <div>
        <h2>Zgłoszenia</h2>
        <ErrorBox message={errorMessage}/>
        <ul className={styles.reportList}>
            {reports.map(r => <li className={styles.reportCard}>
                <ReportCard report={r} handleContentOk={handleContentOk} handleDeleteContent={handleDeleteContent}/>
            </li>)}
        </ul>
    </div>;
}

export interface ReportCardProps {
    report: Report
    handleContentOk: (report: Report) => void
    handleDeleteContent: (report: Report) => void
}

export function ReportCard(props: ReportCardProps) {
    const [showOkConfirmation, setShowOkConfirmation] = useState(false);
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

    return <div>
        <Link to={`/courses/${props.report.courseId}/reviews/${props.report.reviewerUsername}`}>
            <h3>Zgłoszone przez użytkownika {props.report.reportingUsername}</h3>
        </Link>
        <p className={styles.reportReason}>Powód zgłoszenia: {props.report.reason}</p>
        <h3>Treść</h3>
        <p>{props.report.reportedText}</p>
        <div className={styles.reportCardButtonContainer}>{showOkConfirmation
            ? <ConfirmationPopup
                query={"Czy na pewno chcesz usunąć zgłoszenie?"}
                handleConfirmation={() => props.handleContentOk(props.report)}
                setVisibility={setShowOkConfirmation}/>
            : <button onClick={() => setShowOkConfirmation(true)}>Content is ok</button>
        }
        {showDeleteConfirmation
            ? <ConfirmationPopup
                query={"Czy na pewno chcesz usunąć zgłoszoną treść?"}
                handleConfirmation={() => props.handleDeleteContent(props.report)}
                setVisibility={setShowDeleteConfirmation}/>
            : <button onClick={() => setShowDeleteConfirmation(true)}>Delete reported content</button>
        }
        </div>
    </div>
}