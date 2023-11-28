import CourseList from "../../components/CourseList";
import {useEffect, useState} from "react";
import {Course, CourseService} from "../../lib/Course";
import {deselectOptions} from "@testing-library/user-event/dist/select-options";

export function Courses() {
    const [courses, setCourses] = useState<Course[]>([]);
    const [isLoaded, setIsLoaded] = useState(false);
    const [query, setQuery] = useState("");
    const [language, setLanguage] = useState("");
    const [level, setLevel] = useState("");
    const [module, setModule] = useState("");
    const [type, setType] = useState("");

    useEffect(() => {
        CourseService.fetchCoursesByName(query)
            .then(cs => {
                setCourses(cs);
                setIsLoaded(true);
            })
            .catch(e => console.error(e));
    }, [query]);

    const content = isLoaded
        ? <CourseList courses={courses}/>
        : <p>Loading...</p>

    // TODO: allow multiple selections for filters
    // TODO: displaying the default option instead of "Wszystkie"
    return <>
        <h1>Kursy</h1>
        <input type="text" onChange={e => setQuery(e.target.value)}/>

        <select onChange={(e) => setLanguage(e.target.value)}>
            <option value="" selected hidden>Język</option>
            <option value="all">Wszystkie</option>
            <option value="angielski">Angielski</option>
            <option value="hiszpański">Hiszpański</option>
            <option value="włoski">Włoski</option>
            <option value="niemiecki">Niemiecki</option>
            <option value="francuski">Francuski</option>
            <option value="polski">Polski</option>
            <option value="rosyjski">Rosyjski</option>
            <option value="japoński">Japoński</option>
            <option value="chiński">Chiński</option>
            <option value="niderlandzki">Niderlandzki</option>
            <option value="koreański">Koreański</option>
        </select>

        <select onChange={(e)=>setLevel(e.target.value)}>
            <option value="" selected hidden>Poziom</option>
            <option value="all">Wszystkie</option>
            <option value="A1">A1</option>
            <option value="A2">A2</option>
            <option value="B1">B1</option>
            <option value="B2">B2</option>
            <option value="B2+">B2+</option>
            <option value="C1">C1</option>
            <option value="C1+">C1+</option>
            <option value="C2">C2</option>
        </select>

        <select>
            <option value="" selected hidden>Typ</option>
            <option value="all">Wszystkie</option>
            <option value="Ogólny">Ogólny</option>
            <option value="Akademicki">Akademicki</option>
            <option value="Popularno-naukowy">Popularno-naukowy</option>
            <option value="Biznesowy">Biznesowy</option>
            <option value="Konwersacje">Konwersacje</option>
        </select>

        <select onChange={(e) => setModule(e.target.value)}>
            <option value="" selected hidden>Moduł</option>
            <option value="">Wszystkie</option>
            <option value="M1">M1</option>
            <option value="M1/2">M1/2</option>
            <option value="M2">M2</option>
            <option value="M3">M3</option>
            <option value="M4">M4</option>
            <option value="M5">M5</option>
            <option value="M6">M6</option>
            <option value="M7">M7</option>
            <option value="M8">M8</option>
            <option value="M9">M9</option>
            <option value="M10">M10</option>
            <option value="M11">M11</option>
            <option value="M11/12">M11/12</option>
            <option value="M12">M12</option>
            <option value="M13/M13*">M13/M13*</option>
            <option value="M14">M14</option>
            <option value="M15">M15</option>
            <option value="M16">M16</option>
        </select>

        {content}
    </>
}