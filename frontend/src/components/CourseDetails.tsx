import {Course} from "../lib/Course";
import {Teacher, TeacherService} from "../lib/Teacher";
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {COURSE_TEACHER_PLACEHOLDER, NUM_REVIEWS_PLACEHOLDER} from "../lib/utils";


export default function CourseDetails(course: Course) {
    const [teacher, setTeacher] = useState<Teacher | null>(null);
    const [teacherLoaded, setTeacherLoaded] = useState(false);

    useEffect(() => {
        TeacherService.fetchTeacher(course.teacherId)
            .then(t => {
                setTeacher(t);
                setTeacherLoaded(true);
            })

    }, []);

    const teacherContent = (teacher != null && teacherLoaded)
        ? <Link to={"/teachers/" + course.teacherId}> {teacher.name} </Link>
        : COURSE_TEACHER_PLACEHOLDER;

    const moduleContent = course.module !== null
        ? <p>Moduł: {course.module}</p>
        : <p>Ten kurs nie jest przypisany do żadnego modułu</p>

    const levelContent = <p>Poziom: {course.level}</p>
    const typeContent = <p>Typ kursu: {course.type}</p>

    return <>
        <h1>{course.name}</h1>
        <h2>Lektor: {teacherContent}</h2>
        <h3>Informacje o kursie:</h3>
        {moduleContent}
        {levelContent}
        {typeContent}
        <h3>
            Kurs ma {NUM_REVIEWS_PLACEHOLDER} opinii.<br></br>
            <Link to={"/courses/" + course.id}>Czytaj opinie (placeholder)</Link>
        </h3>
    </>
}