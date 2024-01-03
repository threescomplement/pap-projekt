import {Link} from "./utils";
import api from "./api";

export interface Report {
    reportedText: string,
    reportingUsername: string,
    reason: string,
    _links: {
        self: Link,
        entity: Link,
    }
}

async function getAllReports(): Promise<Report[]> {
    const response = await api.get("/api/admin/reports");
    const json = await response.json();
    return json._embedded.reports;
}

async function deleteReport(report: Report): Promise<boolean> {
    const response = await api.delete(report._links.self.href);
    return response.ok;
}

async function deleteReportedEntity(report: Report): Promise<boolean> {
    const response = await api.delete(report._links.entity.href);
    return response.ok;
}

const ReportService = {
    getAllReports,
    deleteReport,
    deleteReportedEntity
};
export default ReportService;