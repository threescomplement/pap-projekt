import {ITeacher} from "../lib/Teacher";
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
    const [teacher, setTeacher] = useState<ITeacher>({
        name: "", _links: [] // is this how we're supposed to initialize it?
    });
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        if (teacherId === undefined) {
            console.error("teacherId is null"); // is this how we're supposed to handle it?
            return;
        }
        fetch(`http://localhost:8080/api/teachers/${Number(teacherId)}`)
            .then(response => response.json())
            .then(json => {
                setTeacher(json);
                setIsLoaded(true);
            })
            .catch(e => console.error(e));
    }, []);

    const content = isLoaded
        ? <TeacherData teacher={teacher} />
        :
        <>
            <h1>{teacherId}</h1>
            <p>Loading...</p>
        </>

    return <>
        {content}
    </>
}