import {Course} from "./Course";
import api from "./api";

export interface Teacher {
    id: string,
    name: string,
    averageRating: string,
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
async function fetchTeacher(teacherId: string): Promise<Teacher> {
    // TODO: teacherId does not exist in the database
    return await api.get(`/teachers/${Number(teacherId)}`)
        .then(response => response.json())
        .catch(e => console.error(e));
}

/**
 * Get the data of all teacher's courses
 */
async function fetchTeacherCourses(teacherId: string): Promise<Course[]> {
    const response = await api.get(`/teachers/${Number(teacherId)}/courses`);
    const json = await response.json();
    return json._embedded.courses;
}

async function fetchTeachersByFilters(filters: TeacherFilters): Promise<Teacher[]> {
    const response = await api.get("/teachers", null, filters);
    const json = await response.json();
    console.log(json);
    return json._embedded.teachers;
}

async function fetchTeacherByCourse(course: Course) {
    return await api.get(course._links.teacher.href)
        .then(response => response.json())
        .catch(e => console.error(e));
}

export const TeacherService = {
    fetchTeacher,
    fetchTeacherByFilters: fetchTeachersByFilters,
    fetchTeacherCourses,
    fetchTeacherByCourse
}