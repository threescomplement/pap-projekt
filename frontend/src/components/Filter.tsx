import React, {useState} from "react";

export const all = "all";

export const languages = [
    "Angielski",
    "Hiszpański",
    "Niemiecki",
    "Włoski",
    "Francuski",
    "Rosyjski",
    "Polski",
    "Japoński",
    "Chiński",
    "Koreański",
    "Niderlandzki"
]

export const levels = ["A1", "A2", "B1", "B2", "B2+", "C1", "C1+", "C2"];
export const modules = [
    "M1",
    "M1/2",
    "M2",
    "M3",
    "M4",
    "M5",
    "M6",
    "M7",
    "M8",
    "M9",
    "M10",
    "M11",
    "M11/12",
    "M12",
    "M13/M13*",
    "M14",
    "M15",
    "M16"
];
export const types = [
    "Ogólny",
    "Akademicki",
    "Popularno-naukowy",
    "Biznesowy",
    "Konwersacje"
];


interface FilterProps {
    name: string;
    options: string[];
    onSelect: (e: React.ChangeEvent<HTMLSelectElement>) => void;
}


export default function Filter(props: FilterProps) {
    const [chosen, setChosen] = useState(all)
    const optionObjects = props.options.map(o => <option value={o} key={o} >{o}</option>)
    return <select onChange={e => {
        props.onSelect(e);
        setChosen(e.target.value)
    }}>
        <option value={all} defaultValue={all}>{chosen !== all ? "Wszystkie": props.name}</option>
        {optionObjects}
    </select>
}
