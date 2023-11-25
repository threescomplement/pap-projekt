import {useEffect, useState} from "react";
import {ITeacher} from "../lib/Teacher";
import TeacherList from "../components/TeacherList";


export function Teachers() {
    const [teachers, setTeachers] = useState<ITeacher[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        fetch(`${process.env.REACT_APP_API_ROOT}teachers`)
            .then(response => response.json())
            .then(json => {
                setTeachers(json._embedded.teachers);
                setIsLoaded(true);
            })
            .catch(e => console.error(e));
    }, []);

    const content = isLoaded
    ?
        <TeacherList teachers={teachers}/>
        :<p>Loading...</p>


    return <>
        <h1>Teachers</h1>
        {content}
        </>
}