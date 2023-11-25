import {ITeacher, attemptTeacherDataRequest, getTeacherCourses} from "../lib/Teacher";
import {useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import CourseList from "../components/CourseList";
import {ICourse} from "../lib/Course";


interface SingleTeacherProps {
    teacher: ITeacher
}

interface TeacherCourseListProps {
    teacherId: string
}

function TeacherData(props: SingleTeacherProps) {
    const teacher = props.teacher
    return <>
        <h1>{teacher.name}</h1>
    </>
}

function TeacherCourseList(props: TeacherCourseListProps) {
    const [courses, setCourses] = useState<ICourse[]>([])
    const teacherId = props.teacherId

    useEffect(() => {
        getTeacherCourses(teacherId)
            .then(c => setCourses(c))
    }, []);

    return <>
        <h3>Courses</h3>
        <CourseList courses={courses}/>
    </>
}


export default function SingleTeacher() {
    const {teacherId} = useParams();
    const [teacher, setTeacher] = useState<ITeacher | null>(null);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        if (teacherId == undefined) {
            console.error("teacherId is null");
            return;
        }
        attemptTeacherDataRequest(teacherId)
            .then(t => {
                    setTeacher(t);
                    setIsLoaded(true);
                }
            )
    }, [teacherId]);

    if (teacher == null || teacherId == null || !isLoaded) {
        return <>
            <h1>{teacherId}</h1>
            <p>Loading...</p>
        </>
    }


    return <>
        <TeacherData teacher={teacher}/>
        <TeacherCourseList teacherId={teacherId}/>
    </>
}