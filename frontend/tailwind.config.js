/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["./src/**/*.{ts,tsx}"],
    theme: {
        extend: {
            colors: {
                'def-bg': '#333',
                'darker-accent': '#222',
                'dark-accent': '#272727',
                'blue-accent': '#66A1BF',
            }
        },
    },
    plugins: [],
    corePlugins: {
        preflight: false,
    },
    prefix: 'tw-',
}