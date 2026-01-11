/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#1967D2', // Superio blue
        secondary: '#F5F7FC', // Superio light background
        accent: '#FFB200', // Superio yellow
        dark: '#202124',
        muted: '#7A7A7A',
        border: '#E0E6F7',
        success: '#00C070',
        danger: '#FF4D4F',
      },
      fontFamily: {
        sans: ['Poppins', 'sans-serif'],
        heading: ['Poppins', 'sans-serif'],
      },
      borderRadius: {
        DEFAULT: '0.5rem',
        lg: '1rem',
        xl: '1.5rem',
      },
      boxShadow: {
        card: '0 2px 16px 0 rgba(25, 103, 210, 0.08)',
        nav: '0 2px 8px 0 rgba(25, 103, 210, 0.06)',
      },
    },
  },
  plugins: [],
}