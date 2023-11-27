import {useParams} from "react-router-dom";


export default function SingleCourse() {
    const {courseId} = useParams();

    return <>
        <h1>{courseId}</h1>
    </>
}