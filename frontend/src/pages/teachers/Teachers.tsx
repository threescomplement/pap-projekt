import {useEffect, useState} from "react";
import {TeacherService, Teacher} from "../../lib/Teacher";
import TeacherList from "../../components/TeacherList";
import Filter, {all, languages} from "../../components/Filter";

export function Teachers() {
    const [teachers, setTeachers] = useState<Teacher[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);
    const [query, setQuery] = useState("");
    const [language, setLanguage] = useState(all);

    useEffect(() => {
        TeacherService.fetchTeacherByFilters({name: query, language: language})
            .then(ts => {
                setTeachers(ts);
                setIsLoaded(true);
            })
            .catch(e => console.error(e));
    }, [query, language]);

    const content = isLoaded
        ?
        <TeacherList teachers={teachers}/>
        : <p>Loading...</p>


    return <>
        <h1>Lektorzy</h1>
        <input type="text" placeholder="Szukaj po nazwisku" onChange={e => setQuery(e.target.value)}/>
        <Filter name="Nauczany język"
                options={languages}
                onSelect={e => setLanguage(e.target.value)}
        />
        {content}
    </>
}