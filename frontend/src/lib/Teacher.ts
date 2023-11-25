import {Course} from "./Course";
import {User} from "./User";
import {authHeader} from "./utils";

export interface Teacher {
    id: string,
    name: string,
    _links: any
}

/**
 * Request the data of a teacher
 * @param teacherId - teacher's id
 */
export async function fetchTeacher(teacherId: string, user: User): Promise<Teacher> {
    // TODO: teacherId does not exist in the database
    return await fetch(`${process.env.REACT_APP_API_ROOT}teachers/${Number(teacherId)}`, {headers: authHeader(user)})
        .then(response => response.json())
        .catch(e => console.error(e));
}

/**
 * Get the data of all teacher's courses
 */
export async function getTeacherCourses(teacherId: string, user: User): Promise<Course[]> {
    const response = await fetch(`${process.env.REACT_APP_API_ROOT}teachers/${Number(teacherId)}/courses`, {headers: authHeader(user)});
    const json = await response.json();
    return json._embedded.courses;
}

export async function fetchTeachers(user: User): Promise<Teacher[]> {
    const response = await fetch(`${process.env.REACT_APP_API_ROOT}teachers`, {headers: authHeader(user)});
    const json = await response.json();
    return json._embedded.teachers;
}