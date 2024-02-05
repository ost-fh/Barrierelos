FROM node:18-alpine

WORKDIR /app

RUN npm install --global serve

COPY ./dist/ ./

EXPOSE 5000

CMD serve --single --listen 5000 /app
