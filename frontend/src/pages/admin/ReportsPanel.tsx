import ReportService, {Report} from "../../lib/Reports";
import {Dispatch, SetStateAction, useEffect, useState} from "react";
import ErrorBox from "../../components/ErrorBox";
import {Link} from "react-router-dom";
import {ConfirmationPopup} from "../../components/ConfirmationPopup";
import styles from "../../ui/pages/AdminPanel.module.css"

export default function ReportsPanel() {
    const [unresolvedReports, setUnresolvedReports] = useState<Report[]>([]);
    const [resolvedReports, setResolvedReports] = useState<Report[]>([]);
    const [errorMessage, setErrorMessage] = useState("");
    const [currentResolvedFilter, setCurrentResolvedFilter] = useState<boolean>(false);

    async function loadReports() {
        try {
            const reports = await ReportService.getAllReports();
            setUnresolvedReports(reports.filter((r) => !r.resolved))
            setResolvedReports(reports.filter((r) => r.resolved))
            setErrorMessage("");
        } catch (e) {
            setErrorMessage("Pobieranie nie powiodło się");
        }
    }

    async function handleContentOk(report: Report) {
        try {
            const ok = await ReportService.resolveReport(report);
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

    return <div className="tw-w-8/12">
        <h2 className="tw-mb-0">Zgłoszenia</h2>
        <ErrorBox message={errorMessage}/>
        <div className="tw-flex tw-mt-6 tw-gap-2 tw-w-full">
            <button className={currentResolvedFilter ? styles.filterButton: styles.activeFilterButton}
                    onClick={() => setCurrentResolvedFilter(false)}>Nierozwiązane</button>
            <button className={!currentResolvedFilter ? styles.filterButton: styles.activeFilterButton}
                    onClick={() => setCurrentResolvedFilter(true)}>Rozwiązane</button>
        </div>
        <ReportList reports={currentResolvedFilter ? resolvedReports : unresolvedReports}
                    setResolvedFilter={setCurrentResolvedFilter} handleContentOk={handleContentOk}
                    handleDeleteContent={handleDeleteContent}/>
    </div>;
}

interface reportListProps {
    reports: Report[]
    setResolvedFilter: Dispatch<SetStateAction<boolean>>
    handleContentOk: (report: Report) => void
    handleDeleteContent: (report: Report) => void
}

function ReportList(p: reportListProps) {
    return <ul className="tw-block tw-bg-darker-accent tw-py-5 tw-px-7 tw-mt-0 tw-rounded-b-lg">
        {/*todo: to the reviewer -  is passing the link as a key ok? theres no other unqiue info in the dto*/}
        {p.reports.map(r => <li
            key={r.reportingUsername + r._links.entity.href} className={styles.reportCard}>
            <ReportCard report={r} handleContentOk={p.handleContentOk} handleDeleteContent={p.handleDeleteContent}/>
        </li>)}
    </ul>
}

export interface ReportCardProps {
    report: Report
    handleContentOk: (report: Report) => void
    handleDeleteContent: (report: Report) => void
}

export function ReportCard(props: ReportCardProps) {
    const [showOkConfirmation, setShowOkConfirmation] = useState(false);
    const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);

    const cardHeader = <h3 className="tw-py-3 tw-leading-7">Zgłoszone przez użytkownika {props.report.reportingUsername}</h3>

    return <div>
        {props.report.resolved
            ? cardHeader
            : <Link className="tw-p-0" to={`/courses/${props.report.courseId}/reviews/${props.report.reviewerUsername}`}>
                {cardHeader}
            </Link>}
        <p className={styles.reportMetadata}>Powód zgłoszenia: {props.report.reason}</p>
        {props.report.resolved && <p
            className={styles.reportMetadata}>Status: {props.report.status} przez użytkownika {props.report.resolvedByUsername}</p>
        }
        <h3 className="tw-pb-2 tw-flex">Treść</h3>
        <p>{props.report.reportedText}</p>
        {props.report.resolved || <div className={styles.reportCardButtonContainer}>{showOkConfirmation
            ? <ConfirmationPopup
                query={"Czy na pewno chcesz usunąć zgłoszenie?"}
                handleConfirmation={() => props.handleContentOk(props.report)}
                setVisibility={setShowOkConfirmation}/>
            : <button onClick={() => setShowOkConfirmation(true)}>Treść w porządku</button>
        }
            {showDeleteConfirmation
                ? <ConfirmationPopup
                    query={"Czy na pewno chcesz usunąć zgłoszoną treść?"}
                    handleConfirmation={() => props.handleDeleteContent(props.report)}
                    setVisibility={setShowDeleteConfirmation}/>
                : <button onClick={() => setShowDeleteConfirmation(true)}>Usuń zgłoszoną treść</button>
            }
        </div>}
    </div>
}