import ReportService, {Report} from "../../lib/Reports";
import {FiAlertCircle, FiCheck} from "react-icons/fi";
import {useEffect, useState} from "react";
import {getDummyReports} from "../../lib/utils";
import ErrorBox from "../../components/ErrorBox";

// TODO styling
export default function ReportsPanel() {
    const [reports, setReports] = useState<Report[]>([]);
    const [errorMessage, setErrorMessage] = useState("");

    function loadReports() {
        try {
            setReports(getDummyReports());  // TODO
        } catch (e) {
            setErrorMessage("Pobieranie nie powiodło się");
        }
    }

    async function handleContentOk(report: Report) {
        try {
            const ok = await ReportService.deleteReport(report);
            if (ok) {
                loadReports();
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
                loadReports();
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
        loadReports();
    })

    return <div>
        <h2>Zgłoszenia</h2>
        <ErrorBox message={errorMessage}/>
        <ul>
            {reports.map(r => <li>
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
    return <div>
        <h3>Zgłoszone przez użytkownika {props.report.reportingUsername}</h3>
        <p>Powód zgłoszenia: {props.report.reason}</p>
        <h3>Treść</h3>
        <p>{props.report.reportedText}</p>
        <button onClick={() => props.handleContentOk(props.report)}><FiCheck/></button>
        <button onClick={() => props.handleDeleteContent(props.report)}><FiAlertCircle/></button>
    </div>
}