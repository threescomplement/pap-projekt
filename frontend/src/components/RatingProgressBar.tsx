import * as Progress from '@radix-ui/react-progress';
import styles from "../ui/components/RatingProgressBar.module.css"

interface RatingProgressBarProps {
    value: string;
}

export default function RatingProgressBar({value}: RatingProgressBarProps) {
    const parsedValue = parseFloat(value)
    const progresBarColor = parsedValue <= 4
        ? '#ff6166'
        : (parsedValue <= 7.5
            ? '#fdfd96'
            : (parsedValue < 9
                ? '#c1e1c1'
                : '#9ee791'));
    //todo: make this use some mapping object instead of 7 chained ternary operators

    const dynamicProgressBarStyles = {
        'backgroundColor': progresBarColor,
        transform: `translateX(-${100 - 10 * parsedValue}%)`
    };

    return <div>
        <Progress.Root className={styles.ProgressRoot}
                       max={10}
                       value={parsedValue}
        >
            <Progress.Indicator className={styles.ProgressIndicator}
                                style={dynamicProgressBarStyles}
            />
        </Progress.Root>
    </div>
};
