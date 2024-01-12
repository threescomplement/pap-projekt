import React, {useEffect, useState} from "react";
import {TeacherService, Teacher} from "../../lib/Teacher";
import Filter, {all, languages} from "../../components/Filter";
import styles from "../../ui/pages/Teachers.module.css";
import {Link} from "react-router-dom";
import {ratingToPercentage} from "../../lib/utils";

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


    return <div className={styles.teachersContainer}>
        <h1 className={styles.teachersHeader}>Lektorzy</h1>
        <input type="text" placeholder="Szukaj po nazwisku" onChange={e => setQuery(e.target.value)}/>
        <Filter name="Nauczany język"
                options={languages}
                onSelect={e => setLanguage(e.target.value)}
        />
        {content}
    </div>
}

interface TeacherListProps {
    teachers: Teacher[]
}

export default function TeacherList({teachers}: TeacherListProps) {
    return <table>
        <tbody>
        <tr id="headers">
            <th>Nazwisko</th>
            <th>Jak łatwe zajęcia?</th>
            <th>Jak interesujące zajęcia?</th>
            <th>Jak bardzo angażuje studentów?</th>
            <th>Liczba opinii</th>
        </tr>
        {teachers.map(t => <TeacherRow teacher={t}/>)}
        </tbody>
    </table>;
}


interface TeacherProps {
    teacher: Teacher
}

function TeacherRow({teacher}: TeacherProps) {
    const hasReviews = parseInt(teacher.numberOfRatings) !== 0;
    return <tr id={teacher.id}>
        <td><Link to={`/teachers/${teacher.id}`}> {teacher.name}</Link></td>
        <td>{hasReviews ? ratingToPercentage(teacher.averageEaseRating) : "N/A"}</td>
        <td>{hasReviews ? ratingToPercentage(teacher.averageInterestRating) : "N/A"}</td>
        <td>{hasReviews ? ratingToPercentage(teacher.averageEngagementRating) : "N/A"}</td>
        <td>{teacher.numberOfRatings}</td>
    </tr>
}