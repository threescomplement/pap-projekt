import api from "./api";
import {Link} from "./utils";

export interface Course {
    id: string,
    name: string,
    language: string,
    type: string,
    level: string,
    module: string | null,
    averageEaseRating: string,
    averageInterestRating: string,
    averageEngagementRating: string,
    teacherId: string,
    numberOfRatings: string,
    teacherName: string,
    _links: {
        self: Link
        teacher: Link
        all: Link
        reviews: Link
    }
}

export interface CourseFilters {
    name: string,
    language: string | null,
    type: string | null,
    level: string | null,
    module: string | null
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

async function fetchCoursesByFilters(filters: CourseFilters): Promise<Course[]> {
    const response = await api.get("/courses", null, filters);
    const json = await response.json();
    return json._embedded.courses;
}

export const CourseService = {
    fetchCourse,
    fetchCourseByFilters: fetchCoursesByFilters
};

