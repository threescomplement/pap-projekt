import {Link} from "./utils";
import api from "./api";
import {Review} from "./Review";
import {ReviewComment} from "./ReviewComment";

export interface Report {
    reportedText: string,
    reportingUsername: string,
    reason: string,
    _links: {
        self: Link,
        entity: Link,
    }
}

export interface ReportRequest {
    reason: string
}

// TODO make sure this works when the backend gets implemented
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

async function reportEntity(entity: Review | ReviewComment, reason: string): Promise<boolean> {
    const body: ReportRequest = {reason};
    const response = await api.post(`${entity._links.self.href}/reports`, body);
    return response.ok;
}

const ReportService = {
    getAllReports,
    deleteReport,
    deleteReportedEntity,
    reportEntity
};
export default ReportService;