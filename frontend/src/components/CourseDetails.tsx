import {Course} from "../lib/Course";
import {Teacher, TeacherService} from "../lib/Teacher";
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {Review, ReviewService} from "../lib/Review";
import {ReviewList} from "./ReviewList";

export default function CourseDetails(course: Course) {
    const [teacher, setTeacher] = useState<Teacher | null>(null);
    const [teacherLoaded, setTeacherLoaded] = useState(false);
    const [reviews, setReviews] = useState<Review[]>([]);

    useEffect(() => {
        TeacherService.fetchTeacherByCourse(course)
            .then(t => {
                setTeacher(t);
                setTeacherLoaded(true);
            })
        ReviewService.fetchReviewsByCourse(course)
            .then(r => {
                setReviews(r);
            })

    }, []);

    const teacherContent = (teacher != null && teacherLoaded)
        ? <Link className="TeacherLink" to={"/teachers/" + course.teacherId}> {teacher.name} </Link>
        : <span className="TeacherLink">COURSE_TEACHER_PLACEHOLDER</span>;

    const moduleContent = course.module !== null
        ? <p className="CourseInfo">Moduł: {course.module}</p>
        : <p className="CourseInfo">Ten kurs nie jest przypisany do żadnego modułu</p>

    const levelContent = <p className="CourseInfo">Poziom: {course.level}</p>
    const typeContent = <p className="CourseInfo">Typ kursu: {course.type}</p>

    const reviewContent = reviews.length == 0
        ? <div>Ten kurs nie ma jeszcze opinii</div>
        : <div>{<ReviewList reviews={reviews}/>}</div>

    return <>
        <h1>{course.name}</h1>
        <p className="TeacherHeader">Lektor: {teacherContent}</p>
        <h3>Informacje o kursie:</h3>
        {moduleContent}
        {levelContent}
        {typeContent}
        <h2 className="OpinionsSection">Opinie</h2>
        {reviewContent}
    </>
}