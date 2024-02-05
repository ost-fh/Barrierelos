import {AppProperties} from "./model.js";

export function loadAppProperties(): AppProperties {
    const user = process.env.RABBITMQ_USER
    if (user === undefined) throw new Error("The environment variable RABBITMQ_USER is not set.")

    const password = process.env.RABBITMQ_PASSWORD
    if (password === undefined) throw new Error("The environment variable RABBITMQ_PASSWORD is not set.")

    const hostname = process.env.RABBITMQ_HOSTNAME
    if (hostname === undefined) throw new Error("The environment variable RABBITMQ_HOSTNAME is not set.")

    const port = process.env.RABBITMQ_PORT
    if (port === undefined) throw new Error("The environment variable RABBITMQ_PORT is not set.")

    return {
        rabbitmqUser: user,
        rabbitmqPassword: password,
        rabbitmqHostname: hostname,
        rabbitmqPort: port,
    }
}
