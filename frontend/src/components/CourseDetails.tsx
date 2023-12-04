import {Course} from "../lib/Course";
import {Teacher, TeacherService} from "../lib/Teacher";
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {COURSE_TEACHER_PLACEHOLDER} from "../lib/utils";

interface CourseDetailsTableElementProps {
    label: string;
    value: string | null;
}


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

    const teacherContent = (teacher != null && teacherLoaded) ? <Link to={"/teachers/" + course.teacherId}>
        {teacher.name}
    </Link> : COURSE_TEACHER_PLACEHOLDER;

    return <>
        <h1>{course.name}</h1>
        <h2>Lektor: {teacherContent}</h2>
    </>
}