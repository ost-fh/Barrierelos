import winston from "winston";

const levels = {
    error: 0,
    warn: 1,
    info: 2,
    http: 3,
    debug: 4,
}

const level = () => {
    const env = process.env.NODE_ENV ?? "development"
    const isDevelopment = env === "development"
    return isDevelopment ? "debug" : "info"
}

const format = winston.format.combine(
    winston.format(info => {
        info.level = info.level.toUpperCase()
        return info;
    })(),
    winston.format.errors({stack: true}),
    winston.format.timestamp({format: "YYYY-MM-DD HH:mm:ss:SSS"}),
    winston.format.colorize({all: true}),
    winston.format.printf((info) => {
        const log = `${info.timestamp} ${info.level}: ${info.message}`
        return info.stack ? `${log}\n${info.stack}` : log;
    }),
)

const colors = {
    error: "red",
    warn: "yellow",
    info: "green",
    http: "magenta",
    debug: "white",
}

winston.addColors(colors)

const transports = [
    new winston.transports.Console(),
]

const Logger = winston.createLogger({
    level: level(),
    levels,
    format,
    transports,
})

export default Logger
