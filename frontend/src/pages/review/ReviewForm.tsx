import {useParams} from "react-router-dom";
import useUser from "../../hooks/useUser";

export function ReviewForm() {
    const {courseId} = useParams();
    const {user} = useUser();

    return <h1>Welcome {user!.username}</h1>
}