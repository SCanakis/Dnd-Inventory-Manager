# ---- Build Angular -----------------------------------------------------
FROM node:20-alpine AS builder
WORKDIR /ui
COPY . .
RUN npm ci && npm run build -- --configuration=docker

# ---- Nginx runtime -----------------------------------------------------
FROM nginx:1.27-alpine
COPY --from=builder /ui/dist/dnd-app /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
