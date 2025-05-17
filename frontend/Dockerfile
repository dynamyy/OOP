FROM node:22 AS build

WORKDIR /app

COPY . .

RUN npm install

RUN npm run build

FROM nginx:stable-alpine AS production

# copy nginx configuration in side conf.d folder
COPY --from=build /app/nginx /etc/nginx/conf.d

# Copy the build output from the dist folder into the Nginx html directory
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port 80 to allow access to the app
EXPOSE 80

# Run Nginx in the foreground
ENTRYPOINT ["nginx", "-g", "daemon off;"]