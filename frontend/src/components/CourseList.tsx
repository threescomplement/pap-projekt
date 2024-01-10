import {Course} from "../lib/Course";
import React from "react";
import {Link} from "react-router-dom";
import {NUM_REVIEWS_PLACEHOLDER, ratingToPercentage} from "../lib/utils";

interface CourseListProps {
    courses: Course[]
}

export default function CourseList({courses}: CourseListProps) {

    return <table>
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
    </table>;
}


interface CourseProps {
    course: Course
}

export function CourseRow({course}: CourseProps) {
    return (
        <tr id={course.id}>
            <td><Link to={`/courses/${course.id}`}> {course.name}</Link></td>
            <td> {ratingToPercentage(course.averageEaseRating)}</td>
            <td> {ratingToPercentage(course.averageInterestRating)}</td>
            <td>{ratingToPercentage(course.averageEngagementRating)}</td>
            <td>{NUM_REVIEWS_PLACEHOLDER}</td>
        </tr>)
}