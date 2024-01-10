import * as Progress from '@radix-ui/react-progress';
import styles from "../ui/components/RatingProgressBar.module.css"

interface RatingProgressBarProps {
    value:string;
}
export default function RatingProgressBar({value}: RatingProgressBarProps) {
    const parsedValue = parseFloat(value)
    return <div>
        <Progress.Root className={styles.ProgressRoot}
        max={10}
        value={parsedValue}
        >
            <Progress.Indicator className={styles.ProgressIndicator}
                                style={{ transform: `translateX(-${100 - 10*parsedValue}%)` }}
            />
        </Progress.Root>
    </div>
};
