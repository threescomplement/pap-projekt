import {Teacher, fetchTeacher, getTeacherCourses} from "../../lib/Teacher";
import {useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import CourseList from "../../components/CourseList";
import {Course} from "../../lib/Course";
import useUser from "../../hooks/useUser";


interface SingleTeacherProps {
    teacher: Teacher
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

function TeacherCourseList({teacherId}: TeacherCourseListProps) {
    const {user} = useUser();
    const [courses, setCourses] = useState<Course[]>([])

    useEffect(() => {
        getTeacherCourses(teacherId, user!)
            .then(c => setCourses(c))
    }, []);

    return <>
        <h3>Courses</h3>
        <CourseList courses={courses}/>
    </>
}


export default function SingleTeacher() {
    const {teacherId} = useParams();
    const {user} = useUser();
    const [teacher, setTeacher] = useState<Teacher | null>(null);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        if (teacherId == undefined) {
            console.error("teacherId is null");
            return;
        }
        fetchTeacher(teacherId, user!)
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