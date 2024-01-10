import UserManagementPanel from "./UserManagementPanel";
import DataImporter from "./DataImporter";
import ReportsPanel from "./ReportsPanel";
import "../../ui/index.css"
import styles from "./AdminPanel.module.css"


export default function AdminPanel() {
    return <div className={"container"}>
        <h1 className={styles.adminHeader}>Panel administratora</h1>
        <DataImporter/>
        <UserManagementPanel/>
        <ReportsPanel/>
    </div>;
}