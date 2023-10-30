FROM node:18-alpine as builder

WORKDIR /app

COPY package*.json ./

RUN npm clean-install

COPY . .

RUN npm run build


FROM node:18-alpine

ENV NODE_ENV production

WORKDIR /app

COPY --from=builder /app/package*.json ./

COPY --from=builder /app/dist/ ./dist

COPY --from=builder /app/.env.production/ ./.env

RUN npm clean-install

CMD npm start
