import styles from "../ui/components/AverageRatingDisplay.module.css";
import {ratingToPercentage} from "../lib/utils";
import React from "react";
import RatingProgressBar from "./RatingProgressBar";
import {Course} from "../lib/Course";
import {Teacher} from "../lib/Teacher";

interface AverageRatingDisplayProps {
    entity: Teacher | Course;
}

export default function AverageRatingDisplay({entity}: AverageRatingDisplayProps) {
    // for the reviewer: is this acceptable? goofy ah polymorphism's
    const isCourse = (entity as Course).language !== undefined;
    const entityToString = isCourse ? "kurs" : "lektor";
    const easeLabel = isCourse ? "Jak łatwy?" : "Jak łatwe zajęcia prowadzi?";
    const interestLabel = isCourse ? "Jak interesujący?" : "Jak interesujące zajęcia prowdzi?";
    const engagementLabel = isCourse ? "Jak angażujący?" : "Jak bardzo angażuje studentów?";
    const hasReviews = entity.numberOfRatings != "0";


    return <div className={styles.averageRatingDisplay}>
        <h2>Uśrednione opinie</h2>
        {hasReviews
            ? <div>
                <div className={styles.singleRating}>
                    <p>{easeLabel} {ratingToPercentage(entity.averageEaseRating)}</p>
                    <RatingProgressBar value={entity.averageEaseRating}/>
                </div>

                <div className={styles.singleRating}>
                    <p>{interestLabel} {ratingToPercentage(entity.averageInterestRating)}</p>
                    <RatingProgressBar value={entity.averageInterestRating}/>
                </div>

                <div className={styles.singleRating}>
                    <p>{engagementLabel} {ratingToPercentage(entity.averageEngagementRating)}</p>
                    <RatingProgressBar value={entity.averageEngagementRating}/>
                </div>
            </div>
            : <p>Ten {entityToString} nie ma jeszcze opinii</p>}
    </div>
}
