import {useEffect, useState} from "react";
import UserService, {AppUser} from "../../lib/User";
import {MdDeleteForever, MdSave} from "react-icons/md";
import ErrorBox from "../../components/ErrorBox";
import EditableText from "../../components/EditableText";

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
            </tr>
            </thead>
            <tbody>
            {users.map(u => <UserTableRow
                key={u.id}
                user={u}
                refreshParent={() => reloadUsers()}
                displayError={setErrorMessage}
            />)}
            </tbody>
        </table>
    </div>
}


interface UserTableRowProps {
    user: AppUser
    displayError: (msg: string) => void,
    refreshParent: () => void
}

function UserTableRow(props: UserTableRowProps) {
    const [editedUser, setEditedUser] = useState(props.user);

    function wasEdited(user: AppUser) {
        return JSON.stringify(user) !== JSON.stringify(props.user);
    }

    function editEmail(newEmail: string) {
        setEditedUser({
            ...editedUser,
            email: newEmail,
        })
    }


    function handleDeleteUser() {
        UserService.deleteUser(props.user)
            .then(ok => ok ? props.refreshParent() : props.displayError("Operacja nie powiodła się"))
            .catch(e => {
                console.error(e);
                props.displayError("Operacja nie powiodła się")
            })
    }

    function handleUpdateUser() {
        UserService.updateUser(editedUser)
            .then(() => props.refreshParent())
            .catch(e => console.error(e))
    }

    return <tr>
        <td>{props.user.id}</td>
        <td>{props.user.username}</td>
        <td>
            <EditableText text={editedUser.email} setText={editEmail}/>
        </td>
        <td>
            <select value={editedUser.role}
                    onChange={e => setEditedUser({...editedUser, role: e.target.value})}
            >
                <option value="ROLE_USER">ROLE_USER</option>
                <option value="ROLE_ADMIN">ROLE_ADMIN</option>
            </select>
        </td>
        <td>
            <input type="checkbox"
                   checked={editedUser.enabled}
                   onChange={() => setEditedUser({...editedUser, enabled: !editedUser.enabled})}
            />
        </td>
        <td>
            <button onClick={handleDeleteUser}><MdDeleteForever/></button>
        </td>
        <td>
            {wasEdited(editedUser) ? <button onClick={handleUpdateUser}><MdSave/></button> : <p>not edited</p>}
        </td>
    </tr>
}