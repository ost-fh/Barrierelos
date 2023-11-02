FROM node:18-alpine

ENV NODE_ENV production

WORKDIR /app

COPY package*.json ./

COPY .env.production/ ./.env

COPY ./dist/ ./dist

RUN npm clean-install

CMD npm start
