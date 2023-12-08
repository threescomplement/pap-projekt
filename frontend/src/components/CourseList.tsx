import {Course} from "../lib/Course";
import React from "react";
import {Link} from "react-router-dom";
import {NUM_REVIEWS_PLACEHOLDER} from "../lib/utils";
import "./CourseList.css"

interface CourseListProps {
    courses: Course[]
}

export default function CourseList({courses}: CourseListProps) {

    return <table>
        <tbody>
        <tr id="headers">
            <th>Nazwa</th>
            <th>Średnia ocena</th>
            <th>Liczba opinii</th>
        </tr>
        {courses.map(c => <CourseRow course={c}/>)}
        </tbody>
    </table>;
}


interface CourseProps {
    course: Course
}

export function CourseRow({course}: CourseProps) {
    return <tr id={course.id}>
        <td><Link to={"/courses/" + course.id}> {course.name}</Link></td>
        <td className="numTableEntry">{course.averageRating}</td>
        <td className="numTableEntry">{NUM_REVIEWS_PLACEHOLDER}</td>
        {/*TODO: replace placeholder*/}
    </tr>
}