import {Link} from "./utils";
import api from "./api";
import {Review} from "./Review";
import {ReviewComment} from "./ReviewComment";

export interface Report {
    reportingUsername: string
    reportedText: string
    reason: string
    courseId: string
    reviewerUsername: string
    resolved: boolean
    resolvedByUsername: string
    resolvedTimestamp: string
    status: string
    _links: {
        self: Link
        entity: Link
        review: Link
    }
}

export interface ReportRequest {
    reportReason: string
}

// TODO more reasonable options
export const reasons = [
    "nieprawdziwe informacje",
    "naruszenie ochrony danych osobowych",
    "nieodpowiedni język",
    "inne"
];

async function getUnresolvedReports(): Promise<Report[]> {
    const response = await api.get("/admin/reports/false");
    const json = await response.json();
    return json._embedded.reports;
}

async function getResolvedReports(): Promise<Report[]> {
    const response = await api.get("/admin/reports/true");
    const json = await response.json();
    return json._embedded.reports;
}

async function getAllReports(): Promise<Report[]> {
    const response = await api.get("/admin/reports/all");
    const json = await response.json();
    return json._embedded.reports;
}
async function resolveReport(report: Report): Promise<boolean> {
    const response = await api.put(`${report._links.self.href}/resolve`, null);
    return response.ok;
}

async function deleteReportedEntity(report: Report): Promise<boolean> {
    const response = await api.delete(report._links.entity.href);
    return response.ok;
}

async function reportEntity(entity: Review | ReviewComment, reportReason: string): Promise<boolean> {
    const body: ReportRequest = {reportReason};
    const response = await api.post(`${entity._links.self.href}/reports`, body);
    return response.ok;
}

const ReportService = {
    getAllReports,
    getUnresolvedReports,
    getResolvedReports,
    resolveReport,
    deleteReportedEntity,
    reportEntity
};
export default ReportService;