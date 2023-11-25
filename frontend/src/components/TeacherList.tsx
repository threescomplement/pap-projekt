import {Teacher} from "../lib/Teacher";
import React from "react";
import {Link} from "react-router-dom";

interface TeacherListProps {
    teachers: Teacher[]
}

export default function TeacherList({teachers}: TeacherListProps) {
    return <table>
        {teachers.map(t => <TeacherRow teacher={t}/>)}
    </table>;
}


interface TeacherProps {
    teacher: Teacher
}

export function TeacherRow({teacher}: TeacherProps) {
    return <tr>
        <td><Link to={"/teachers/" + teacher.id}> {teacher.name}</Link></td>
    </tr>
}