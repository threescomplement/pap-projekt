import UserManagementPanel from "./UserManagementPanel";
import DataImporter from "./DataImporter";
import ReportsPanel from "./ReportsPanel";
import "../../ui/index.css"
import styles from "../../ui/pages/AdminPanel.module.css"


export default function AdminPanel() {
    return <div className={"container"}>
        <h1 className={styles.adminHeader}>Panel administratora</h1>
        <DataImporter/>
        <div className={styles.managementAndReportContainer}>
        <UserManagementPanel/>
        <ReportsPanel/>
        </div>
    </div>;
}