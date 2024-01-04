import {useEffect, useState} from "react";
import UserService, {AppUser} from "../../lib/User";
import user from "../../lib/User";

export default function UserManagementPanel() {
    const [users, setUsers] = useState<AppUser[]>([]);

    useEffect(() => {
        UserService.getAllUsers()
            .then(users => setUsers(users))
    })

    return <div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Enabled</th>
            </tr>
            </thead>
            <tbody>
                {users.map(u => <UserTableRow user={u}/>)}
            </tbody>
        </table>
    </div>
}


interface UserTableRowProps {
    user: AppUser
}
function UserTableRow({user}: UserTableRowProps) {
    return <tr>
        <td>{user.id}</td>
        <td>{user.username}</td>
        <td>{user.email}</td>
        <td>{user.role}</td>
        <td>{user.enabled ? "enabled" : "disabled"}</td>
    </tr>
}