
import styles from '../ui/pages/Home.module.css';

export function Home() {
    return (
        <div className={styles.homeContainer}>
            <h1>Home</h1>
            <p>API root {process.env.REACT_APP_API_ROOT}</p>
            <p>Environment {process.env.NODE_ENV}</p>
        </div>
    );
}