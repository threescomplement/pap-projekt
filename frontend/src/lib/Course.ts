import api from "./api";
import {commonElements} from "./utils";

export interface Course {
    id: string,
    name: string,
    language: string,
    type: string | null, // TODO: can type be null?
    level: string | null, // TODO: can level be null?
    module: string | null, //TODO: make api return teacher id
    links: any
}

export interface CourseFilters {
    query: string,
    language: string,
    type: string,
    level: string,
    module: string
}

/**
 * Request the data of a course
 * @param courseId - id of the course
 */
async function fetchCourse(courseId: string): Promise<Course> {
    // TODO: courseId does not exist in the database
    const response = await api.get(`/courses/${Number(courseId)}`);
    return await response.json();
}

async function fetchCoursesByName(name: string): Promise<Course[]> {
    const response = await api.get(`/courses/search/findCoursesByNameContaining?name=${name}`);
    const json = await response.json();
    return json._embedded.courses;
}

async function fetchCoursesByLanguage(language: string): Promise<Course[]> {
    const response = await api.get(`/courses/search/findCoursesByLanguageContaining?language=${language}`);
    const json = await response.json();
    return json._embedded.courses;
}

async function fetchCoursesByType(type: string): Promise<Course[]> {
    const response = await api.get(`/courses/search/findCoursesByTypeContaining?type=${type}`);
    const json = await response.json();
    return json._embedded.courses;
}

async function fetchCoursesByModule(module: string): Promise<Course[]> {
    const response = await api.get(`/courses/search/findCoursesByModuleContaining?module=${module}`);
    const json = await response.json();
    return json._embedded.courses;
}

async function fetchCoursesByLevel(level: string): Promise<Course[]> {
    const response = await api.get(`/courses/search/findCoursesByLevelContaining?level=${level}`);
    const json = await response.json();
    return json._embedded.courses;
}

async function fetchCourseByFilters({query, type, module, level, language}: CourseFilters) {
    return commonElements([
        await fetchCoursesByName(query),
        await fetchCoursesByType(type),
        await fetchCoursesByModule(module),
        await fetchCoursesByLevel(level),
        await fetchCoursesByLanguage(language)])
}

export const CourseService = {
    fetchCourse,
    fetchCourseByFilters
};

