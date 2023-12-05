import {Course} from "../lib/Course";
import {Teacher, TeacherService} from "../lib/Teacher";
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";

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
        ? <Link className="TeacherLink" to={"/teachers/" + course.teacherId}> {teacher.name} </Link>
        : <span className="TeacherLink">COURSE_TEACHER_PLACEHOLDER</span>;

    const moduleContent = course.module !== null
        ? <p className="CourseInfo">Moduł: {course.module}</p>
        : <p className="CourseInfo">Ten kurs nie jest przypisany do żadnego modułu</p>

    const levelContent = <p className="CourseInfo">Poziom: {course.level}</p>
    const typeContent = <p className="CourseInfo">Typ kursu: {course.type}</p>

    return <>
        <h1>{course.name}</h1>
        <p className="TeacherHeader">Lektor: {teacherContent}</p>
        <h3>Informacje o kursie:</h3>
        {moduleContent}
        {levelContent}
        {typeContent}
        <h2 className="OpinionsSection">Opinie</h2>
    </>
}