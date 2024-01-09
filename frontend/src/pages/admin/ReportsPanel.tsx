import ReportService, {Report} from "../../lib/Reports";
import {FiAlertCircle, FiCheck} from "react-icons/fi";
import {useEffect, useState} from "react";
import ErrorBox from "../../components/ErrorBox";
import {Link} from "react-router-dom";

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
    //TODO refactor this, include the params in the DTO instead of relying on links being the same
    function extractLink(report: Report): string {
        const baseLink = report._links.review.href;
        const idx = baseLink.indexOf("/api") + 4;
        return baseLink.substring(idx);
    }

    return <div>
        <Link to={extractLink(props.report)}>
            <h3>Zgłoszone przez użytkownika {props.report.reportingUsername}</h3>
        </Link>
        <p>Powód zgłoszenia: {props.report.reason}</p>
        <h3>Treść</h3>
        <p>{props.report.reportedText}</p>
        <button onClick={() => props.handleContentOk(props.report)}><FiCheck/></button>
        <button onClick={() => props.handleDeleteContent(props.report)}><FiAlertCircle/></button>
    </div>
}