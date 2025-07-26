# Build stage
FROM node:20-alpine AS builder
WORKDIR /ui
COPY . .
# Use npm install instead of npm ci to handle lock file mismatches
RUN npm install && npm run build -- --configuration=docker

# Runtime stage
FROM nginx:1.27-alpine
COPY --from=builder /ui/dist/dnd-app/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80