import {TeacherService, Teacher} from "../../lib/Teacher";
import {useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import CourseList from "../../components/CourseList";
import {Course} from "../../lib/Course";
import {ratingToPercentage} from "../../lib/utils";


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
        <h2>Uśrednione opinie</h2>
        <div>
            <p>Jak łatwe zajęcia prowadzi? {ratingToPercentage(teacher.averageEaseRating)}</p>
            <p>Jak interesujące zajęcia prowadzi? {ratingToPercentage(teacher.averageInterestRating)}</p>
            <p>Jak bardzo angażuje studentów? {ratingToPercentage(teacher.averageEngagementRating)}</p>
        </div>
    </>
}

function TeacherCourseList({teacherId}: TeacherCourseListProps) {
    const [courses, setCourses] = useState<Course[]>([])

    useEffect(() => {
        TeacherService.fetchTeacherCourses(teacherId)
            .then(c => setCourses(c))
    }, [teacherId]);

    return <div>
        <h2>Kursy</h2>
        <CourseList courses={courses}/>
        </div>
}


export default function SingleTeacher() {
    const {teacherId} = useParams();
    const [teacher, setTeacher] = useState<Teacher | null>(null);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        if (teacherId == null) {
            console.error("teacherId is null");
            return;
        }
        TeacherService.fetchTeacher(teacherId)
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