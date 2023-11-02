import {ICourse} from "../lib/Course";
import {useParams} from "react-router-dom";

interface SingleCourseProps {
    course: ICourse
}

export default function SingleCourse() {
    const {courseId} = useParams();

    return <>
        <h1>{courseId}</h1>
    </>
}