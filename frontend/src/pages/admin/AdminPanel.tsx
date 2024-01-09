import UserManagementPanel from "./UserManagementPanel";
import DataImporter from "./DataImporter";
import ReportsPanel from "./ReportsPanel";


export default function AdminPanel() {
    return <>
        <h1>Panel administratora</h1>
        <DataImporter/>
        <UserManagementPanel/>
        <ReportsPanel/>
    </>;
}