import {Course} from "../lib/Course";
import React from "react";
import {Link} from "react-router-dom";
import {ratingToPercentage} from "../lib/utils";

interface CourseListProps {
    courses: Course[]
}

export default function CourseList({courses}: CourseListProps) {

    return courses.length !== 0 ?<table>
        <tbody>
        <tr id="headers">
            <th>Nazwa</th>
            <th>Jak łatwy?</th>
            <th>Jak interesujący?</th>
            <th>Jak angażujący?</th>
            <th>Liczba opinii</th>
        </tr>
        {courses.map(c => <CourseRow course={c}/>)}
        </tbody>
    </table> : <p>Ten nauczyciel nie ma jeszcze kursów</p>;
    /* todo: styling */
}


interface CourseProps {
    course: Course
}

export function CourseRow({course}: CourseProps) {
    const hasRatings = parseInt(course.numberOfRatings) !== 0;
    return (
        <tr id={course.id}>
            <td><Link to={`/courses/${course.id}`}> {course.name}</Link></td>
            <td> {hasRatings ? ratingToPercentage(course.averageEaseRating) : "N/A"}</td>
            <td> {hasRatings ? ratingToPercentage(course.averageInterestRating) : "N/A"}</td>
            <td>{hasRatings ? ratingToPercentage(course.averageEngagementRating) : "N/A"}</td>
            <td>{course.numberOfRatings}</td>
        </tr>)
}