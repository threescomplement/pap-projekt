import * as Progress from '@radix-ui/react-progress';
import styles from "../ui/components/RatingProgressBar.module.css"

interface RatingProgressBarProps {
    value:number
}
export default function RatingProgressBar({value}: RatingProgressBarProps) {
    return <div>
        <Progress.Root className={styles.ProgressRoot}
        max={10}
        value={value}
        >
            <Progress.Indicator className={styles.ProgressIndicator}
                                style={{ transform: `translateX(-${100 - 10*value}%)` }}
            />
        </Progress.Root>
    </div>
};
