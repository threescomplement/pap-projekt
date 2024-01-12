import {Course} from "../lib/Course";
import React from "react";
import {Link} from "react-router-dom";
import {ratingToPercentage} from "../lib/utils";

interface CourseListProps {
    courses: Course[]
    showTeacherName: boolean
}

export default function CourseList({courses, showTeacherName}: CourseListProps) {

    return courses.length !== 0 ? <table>
        <tbody>
        <tr id="headers">
            <th>Nazwa</th>
            {showTeacherName && <th>Lektor</th>}
            <th>Jak łatwy?</th>
            <th>Jak interesujący?</th>
            <th>Jak angażujący?</th>
            <th>Liczba opinii</th>
        </tr>
        {courses.map(c => <CourseRow key={c.id} course={c} showTeacherName={showTeacherName}/>)}
        </tbody>
    </table> : <p>Brak kursów do wyświetlenia</p>;
    /* todo: styling */
}


interface CourseProps {
    course: Course
    showTeacherName: boolean
}

export function CourseRow({course, showTeacherName}: CourseProps) {
    const hasRatings = parseInt(course.numberOfRatings) !== 0;

    return (
        <tr id={course.id}>
            <td><Link to={`/courses/${course.id}`}> {course.name}</Link></td>
            {showTeacherName && <td><Link to={`/teachers/${course.teacherId}`}>{course.teacherName}</Link></td>}
            <td> {hasRatings ? ratingToPercentage(course.averageEaseRating) : "N/A"}</td>
            <td> {hasRatings ? ratingToPercentage(course.averageInterestRating) : "N/A"}</td>
            <td>{hasRatings ? ratingToPercentage(course.averageEngagementRating) : "N/A"}</td>
            <td>{course.numberOfRatings}</td>
        </tr>)
}