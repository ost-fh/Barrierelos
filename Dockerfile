FROM node:18-alpine as builder

WORKDIR /app

COPY package*.json ./

RUN npm clean-install

COPY . .

RUN npm run build


FROM node:18-alpine

WORKDIR /app

RUN npm install --global serve

COPY --from=builder /app/dist/ .

EXPOSE 5000

CMD serve --single --listen 5000 /app
