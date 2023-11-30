import {buildParamsString} from "./api";

test('Renders header', () => {
    expect(buildParamsString({language: null, type: "Akademicki"})).toBe("?type=Akademicki")
});