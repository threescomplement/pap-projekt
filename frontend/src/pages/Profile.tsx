import {User} from "../lib/User";


interface ProfileProps {
    user: User | null
}
export default function Profile({user}: ProfileProps) {
    if (user == null) {
        return <h1>Log in to view your profile</h1>;
    }

    return <>
        <h1>My profile</h1>
        <p>Username: {user.username}</p>
        <p>Email: {user.email}</p>
        <p>Roles: {user.roles}</p>
    </>;
}