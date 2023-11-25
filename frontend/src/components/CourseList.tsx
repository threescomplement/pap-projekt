import {Course} from "../lib/Course";
import React from "react";
import {Link} from "react-router-dom";

interface CourseListProps {
    courses: Course[]
}

export default function CourseList({courses}: CourseListProps) {

    return <table>
        {courses.map(c => <CourseRow course={c}/>)}
    </table>;
}


interface CourseProps {
    course: Course
}

export function CourseRow({course}: CourseProps) {
    return <tr>
    <td><Link to={"/courses/" + course.id}> {course.name}</Link></td>
    </tr>
}