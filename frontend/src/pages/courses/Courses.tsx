import CourseList from "../../components/CourseList";
import React, {useEffect, useState} from "react";
import {Course, CourseService, } from "../../lib/Course";
import Filter, {all, languages, levels, modules, types} from "../../components/Filter";


export function Courses() {
    const [courses, setCourses] = useState<Course[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);
    const [query, setQuery] = useState("");
    const [language, setLanguage] = useState<string>(all);
    const [level, setLevel] = useState<string>(all);
    const [module, setModule] = useState<string>(all);
    const [type, setType] = useState<string>(all);

    useEffect(() => {
        CourseService.fetchCourseByFilters({
            name: query,
            type,
            module,
            level,
            language,
        })
            .then(cs => {
                setCourses(cs);
                setIsLoaded(true);
            })
            .catch(e => console.error(e));
    }, [query, language, type, level, module]);

    const content = isLoaded
        ? <CourseList courses={courses}/>
        : <p>Loading...</p>

    // TODO: allow multiple selections for filters
    return <>
        <h1>Kursy</h1>
        <input type="text" placeholder="Szukaj po nazwie" onChange={e => setQuery(e.target.value)}/>

        <Filter
            name={"Język"}
            options={languages}
            onSelect={(e) => setLanguage(e.target.value)}
        />
        <Filter name={"Poziom"}
                options={levels}
                onSelect={(e) => setLevel(e.target.value)}
        />
        <Filter
            name={"Typ"}
            options={types}
            onSelect={(e) => setType(e.target.value)}
        />
        <Filter
            name={"Moduł"}
            options={modules}
            onSelect={(e) => setModule(e.target.value)}/>
        {content}
    </>
}