import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/checkins': {
        target: 'http://localhost:8086',
        changeOrigin: true,
      },
      '/events': {
        target: 'http://localhost:8086',
        changeOrigin: true,
      },
      '/v3': {
        target: 'http://localhost:8086',
        changeOrigin: true,
      },
      '/swagger-ui': {
        target: 'http://localhost:8086',
        changeOrigin: true,
      },
    },
  },
});
