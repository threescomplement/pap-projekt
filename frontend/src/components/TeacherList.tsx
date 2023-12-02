import {Teacher} from "../lib/Teacher";
import React from "react";
import {Link} from "react-router-dom";

interface TeacherListProps {
    teachers: Teacher[]
}

export default function TeacherList({teachers}: TeacherListProps) {
    return <table>
        <tbody>
        <tr>
            <td>Nazwisko</td>
            <td>Średnia ocena</td>
            <td>Liczba opinii</td>
        </tr>
        </tbody>
        {teachers.map(t => <TeacherRow teacher={t}/>)}
    </table>;
}


interface TeacherProps {
    teacher: Teacher
}

export function TeacherRow({teacher}: TeacherProps) {
    return <tr>
        <td>
            <Link to={"/teachers/" + teacher.id}> {teacher.name}</Link>
        </td>
        <td style = {{textAlign: 'right'}}>
            {teacher.averageRating}
        </td>
        <td style={{textAlign: 'right'}}>
            x
        </td>
    </tr>
}