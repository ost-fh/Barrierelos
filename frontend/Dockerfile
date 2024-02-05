FROM node:18 as build
COPY . /
RUN npm ci --cache .npm --prefer-offline
RUN npm run build

FROM node:18-alpine

WORKDIR /app

RUN npm install --global serve

COPY --from=build ./dist/ ./

EXPOSE 5000

CMD serve --single --listen 5000 /app
