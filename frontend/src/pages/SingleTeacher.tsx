import {ITeacher, attemptTeacherDataRequest} from "../lib/Teacher";
import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";


interface SingleTeacherProps {
    teacher: ITeacher
}

function TeacherData(props: SingleTeacherProps) {
    const teacher = {...props}.teacher
    return <>
        <h1>{teacher.name}</h1>
        <a href={teacher._links.courses.href}>courses</a>
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
    }, []);

    if (teacher == null) {
        return <>
            <h1>{teacherId}</h1>
            <p>Loading...</p>
        </>
    }


    return <>
        <TeacherData teacher={teacher}/>
    </>
}