import React, { useEffect } from "react";
import {useNavigate, useParams} from "react-router-dom";
import styles from "./IntermediatePage.module.css";

export function ReviewDeleted() {
    const navigate = useNavigate();
    const {courseId }= useParams();

    useEffect(() => {
        const redirectTimeout = setTimeout(() => {
            navigate(`/courses/${courseId}`);
        }, 3000);

        return () => clearTimeout(redirectTimeout);
    }, [courseId, navigate]);

    return (
        <div className={styles.intermediatePage}>
            <p>Ocena została usunięta</p>
            <p>Wkrótce zostaniesz przekierowany...</p>
        </div>
    );
}
