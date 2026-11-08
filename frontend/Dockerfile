# ---------- build stage ----------
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# ---------- runtime stage ----------
FROM nginx:1.27-alpine
# nginx renders templates/*.template with env vars at startup
COPY nginx/default.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist /usr/share/nginx/html
# Backend address (matches the docker-compose service / Kubernetes Service name)
ENV BACKEND_HOST=jingle-backend \
    BACKEND_PORT=8080
EXPOSE 80
