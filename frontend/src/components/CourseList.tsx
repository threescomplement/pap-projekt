import {Course} from "../lib/Course";
import React from "react";
import {Link} from "react-router-dom";
import {NUM_REVIEWS_PLACEHOLDER} from "../lib/utils";

interface CourseListProps {
    courses: Course[]
}

export default function CourseList({courses}: CourseListProps) {

    return <table>
        <tbody>
        <tr>
            <td>Nazwa</td>
            <td>Średnia ocena</td>
            <td>Liczba opinii</td>
        </tr>
        </tbody>
        {courses.map(c => <CourseRow course={c}/>)}
    </table>;
}


interface CourseProps {
    course: Course
}

export function CourseRow({course}: CourseProps) {
    return <tr id={course.id}>
        <td><Link to={"/courses/" + course.id}> {course.name}</Link></td>
        <td style={{textAlign: 'right'}}>{course.averageRating}</td>
        <td style={{textAlign: 'right'}}>{NUM_REVIEWS_PLACEHOLDER}</td>
    </tr>
}