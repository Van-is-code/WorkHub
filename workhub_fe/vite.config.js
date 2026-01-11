import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // 👈 bắt buộc để nhận kết nối từ bên ngoài
    strictPort: true,
    port: 5173,
    allowedHosts: ['.ngrok-free.app'], // 👈 cho phép tất cả subdomain ngrok
    cors: true,
  }
})
