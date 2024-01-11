import {Course} from "../lib/Course";
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {ratingToPercentage} from "../lib/utils";
import {Teacher, TeacherService} from "../lib/Teacher";

interface CourseListProps {
    courses: Course[]
}

export default function CourseList({courses}: CourseListProps) {

    return courses.length !== 0 ? <table>
        <tbody>
        <tr id="headers">
            <th>Nazwa</th>
            <th>Lektor</th>
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
    const [teacherName, setTeacherName] = useState<null | string>(null);
    useEffect(() => {
        TeacherService.fetchTeacher(course.teacherId)
            .then(t=>setTeacherName(t.name))
    }, []);


    return (
        <tr id={course.id}>
            <td><Link to={`/courses/${course.id}`}> {course.name}</Link></td>
            {teacherName != null && <td><Link to={`/teachers/${course.teacherId}`}>{teacherName}</Link></td>}
            <td> {hasRatings ? ratingToPercentage(course.averageEaseRating) : "N/A"}</td>
            <td> {hasRatings ? ratingToPercentage(course.averageInterestRating) : "N/A"}</td>
            <td>{hasRatings ? ratingToPercentage(course.averageEngagementRating) : "N/A"}</td>
            <td>{course.numberOfRatings}</td>
        </tr>)
}