import {useEffect, useState} from "react";
import {fetchTeachers, Teacher} from "../../lib/Teacher";
import TeacherList from "../../components/TeacherList";


export function Teachers() {
    const [teachers, setTeachers] = useState<Teacher[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);

    useEffect(() => {
        fetchTeachers()
            .then(ts => {
                setTeachers(ts);
                setIsLoaded(true);
            })
            .catch(e => console.error(e));
    }, []);

    const content = isLoaded
        ?
        <TeacherList teachers={teachers}/>
        : <p>Loading...</p>


    return <>
        <h1>Lektorzy</h1>
        {content}
    </>
}