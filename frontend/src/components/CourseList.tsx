import {ICourse} from "../lib/Course";
import React from "react";
import {Link} from "react-router-dom";

interface CourseListProps {
    courses: ICourse[]
}

export default function CourseList({courses}: CourseListProps) {

    return <table>
        {courses.map(c => <Course course={c}/>)}
    </table>;
}


interface CourseProps {
    course: ICourse
}

export function Course({course}: CourseProps) {
    return <tr>
    <td><Link to="1"> {course.name}</Link></td>
    </tr>
}