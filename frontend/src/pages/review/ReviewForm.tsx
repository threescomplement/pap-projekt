import {useParams} from "react-router-dom";
import useUser from "../../hooks/useUser";
import {useEffect, useState} from "react";
import {Course, CourseService} from "../../lib/Course";
import "./ReviewForm.css"

export function ReviewForm() {
    const {user} = useUser();
    const {courseId} = useParams();
    const [course, setCourse] = useState<Course | null>(null);
    const [rating, setRating] = useState<number | null>(null);

    useEffect(() => {
        CourseService.fetchCourse(courseId!)
            .then(c => {
                setCourse(c);
            })
    }, []);

    function RatingSlider() {
        const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
            const value = Math.round(parseFloat(e.target.value));
            setRating(value);
        };

        return (
            <input
                type="range"
                min={1}
                max={10}
                step={1}
                value={rating !== null ? rating : 1}
                onChange={handleChange}
                onInput={handleChange}
            />
        );
    }

    return <div className="review-form">
        <h1>Napisz opinię {course !== null && ("do kursu " + course.name)}</h1>
        <textarea placeholder="Co spodobało ci się w kursie, a co należy poprawić?"></textarea>
        <p>{rating !== null ? "Twoja ocena: " + rating: "Wybierz ocenę:"} </p>
        <div><RatingSlider/></div>
        <div>
            <button>Zatwierdź</button>
        </div>
    </div>
}

