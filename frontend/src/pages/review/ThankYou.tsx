import React, { useEffect } from "react";
import {useNavigate, useParams} from "react-router-dom";
import styles from "./IntermediatePage.module.css";

export function ThankYouPage() {
    const navigate = useNavigate();
    const {courseId }= useParams();

    useEffect(() => {
        const redirectTimeout = setTimeout(() => {
            navigate("/courses/" + courseId);
        }, 3000);

        return () => clearTimeout(redirectTimeout);
    }, [courseId, navigate]);

    return (
        <div className={styles.intermediatePage}>
            <h1>Dziękujemy!</h1>
            <p>Twoja opinia została zarejestrowana.</p>
            <p>Wkrótce zostaniesz przekierowany...</p>
        </div>
    );
}
