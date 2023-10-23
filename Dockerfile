FROM node:18-alpine as builder

WORKDIR /app

COPY package*.json ./

RUN npm clean-install

COPY . .

RUN npm run build


FROM node:18-alpine

WORKDIR /app

COPY --from=builder /app/package*.json ./

COPY --from=builder /app/dist/ ./dist

ENV NODE_ENV production

RUN npm clean-install

CMD npm start
