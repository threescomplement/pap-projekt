import {Teacher} from "../lib/Teacher";
import React from "react";
import {Link} from "react-router-dom";
import {NUM_REVIEWS_PLACEHOLDER} from "../lib/utils";
import "./CourseList.css"

interface TeacherListProps {
    teachers: Teacher[]
}

export default function TeacherList({teachers}: TeacherListProps) {
    return <table>
        <tbody>
        <tr id="headers">
            <td>Nazwisko</td>
            <td>Średnia ocena</td>
            <td>Liczba opinii</td>
        </tr>
        {teachers.map(t => <TeacherRow teacher={t}/>)}
        </tbody>
    </table>;
}


interface TeacherProps {
    teacher: Teacher
}

export function TeacherRow({teacher}: TeacherProps) {
    return <tr id={teacher.id}>
        <td>
            <Link to={"/teachers/" + teacher.id}> {teacher.name}</Link>
        </td>
        <td className="numTableEntry">
            {teacher.averageRating}
        </td>
        <td className="numTableEntry">
            {NUM_REVIEWS_PLACEHOLDER}
        </td>
    </tr>
}