import {Course} from "./Course";
import api from "./api";

export interface Teacher {
    id: string,
    name: string,
    _links: any
}

export interface TeacherFilters {
    name: String
    language: String
}

/**
 * Request the data of a teacher
 * @param teacherId - teacher's id
 */
export async function fetchTeacher(teacherId: string): Promise<Teacher> {
    // TODO: teacherId does not exist in the database
    return await api.get(`/teachers/${Number(teacherId)}`)
        .then(response => response.json())
        .catch(e => console.error(e));
}

/**
 * Get the data of all teacher's courses
 */
export async function fetchTeacherCourses(teacherId: string): Promise<Course[]> {
    const response = await api.get(`/teachers/${Number(teacherId)}/courses`);
    const json = await response.json();
    return json._embedded.courses;
}

export async function fetchTeachersByFilters(filters: TeacherFilters): Promise<Teacher[]> {
    const response = await api.get("/teachers", null, filters);
    const json = await response.json();
    console.log(json);
    return json._embedded.teachers;
}

export const TeacherService = {
    fetchTeacher,
    fetchTeacherByFilters: fetchTeachersByFilters,
    fetchTeacherCourses
}