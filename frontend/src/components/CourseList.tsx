import {Course} from "../lib/Course";
import React from "react";
import {Link} from "react-router-dom";
import {NUM_REVIEWS_PLACEHOLDER} from "../lib/utils";
import styles from "./CourseList.module.css"

interface CourseListProps {
    courses: Course[]
}

export default function CourseList({courses}: CourseListProps) {

    return <table className={styles.courseList}>
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
    return (
        <tr id={course.id} className={styles.courseRow}>
            <td><Link to={"/courses/" + course.id} className={styles.courseLink}> {course.name}</Link></td>
            <td className={`${styles.numTableEntry} ${styles.courseAvgRating}`}>{course.averageRating}</td>
            <td className={`${styles.numTableEntry} ${styles.courseNumReviews}`}>{NUM_REVIEWS_PLACEHOLDER}</td>
        </tr>)
}