import {useEffect, useState} from "react";
import UserService, {AppUser} from "../../lib/User";
import {MdDeleteForever} from "react-icons/md";
import ErrorBox from "../../components/ErrorBox";

export default function UserManagementPanel() {
    const [users, setUsers] = useState<AppUser[]>([]);
    const [errorMessage, setErrorMessage] = useState("");

    function reloadUsers() {
        UserService.getAllUsers()
            .then(users => setUsers(users))
    }

    useEffect(() => reloadUsers(), []);

    return <div>
        <h2>Użytkownicy</h2>
        <ErrorBox message={errorMessage}/>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Username</th>
                <th>Email</th>
                <th>Role</th>
                <th>Enabled</th>
                {/*for the buttons*/}
                <th>Delete</th>
            </tr>
            </thead>
            <tbody>
            {users.map(u => <UserTableRow
                key={u.id}
                user={u}
                afterDelete={() => reloadUsers()}
                displayError={setErrorMessage}
            />)}
            </tbody>
        </table>
    </div>
}


interface UserTableRowProps {
    user: AppUser
    displayError: (msg: string) => void,
    afterDelete: () => void
}

function UserTableRow(props: UserTableRowProps) {
    function handleDeleteUser() {
        UserService.deleteUser(props.user)
            .then(ok => ok ? props.afterDelete() : props.displayError("Operacja nie powiodła się"))
            .catch(e => {
                console.error(e);
                props.displayError("Operacja nie powiodła się")
            })
    }

    //TODO editing user
    return <tr>
        <td>{props.user.id}</td>
        <td>{props.user.username}</td>
        <td>{props.user.email}</td>
        <td>{props.user.role}</td>
        <td>{props.user.enabled ? "enabled" : "disabled"}</td>
        <td>
            <button onClick={handleDeleteUser}><MdDeleteForever/></button>
        </td>
    </tr>
}