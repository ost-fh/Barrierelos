import {AMQPClient, AMQPMessage} from "@cloudamqp/amqp-client";
import {AnalysisJob, AppProperties} from "./model.js";
import {analyzeWebsite} from "./scanner.js";
import {formatFailedWebsiteResult} from "./formatter.js";
import Logger from "./logger.js";

export async function subscribe(props: AppProperties) {
    const user = encodeURIComponent(props.rabbitmqUser)
    const password = encodeURIComponent(props.rabbitmqPassword)
    const hostname = encodeURI(props.rabbitmqHostname)
    const port = props.rabbitmqPort
    const url = `amqp://${user}:${password}@${hostname}:${port}`

    Logger.info(`Attempting to connect to RabbitMQ at ${url.replace(`:${password}`, ":<password>")}`)
    const amqpClient = new AMQPClient(url)
    const connection = await amqpClient.connect()
    Logger.info(`Connected to RabbitMQ`)

    const channel = await connection.channel()
    await channel.basicQos(1)
    const jobQueue = await channel.queue("barrierelos.analysis.job")
    const resultQueue = await channel.queue("barrierelos.analysis.result")
    const consumer = await jobQueue.subscribe({exclusive: true, noAck: false}, handleMessage)

    await consumer.wait()
    await connection.close()

    async function handleMessage(msg: AMQPMessage) {
        const bodyString = msg.bodyString()
        if (bodyString === null) {
            const errorMessage = "Received a message without a body"
            Logger.error(errorMessage)
            const failResult = formatFailedWebsiteResult(errorMessage)
            await resultQueue.publish(JSON.stringify(failResult))
            await msg.reject(false)
            return
        }

        const job: AnalysisJob = JSON.parse(bodyString) as AnalysisJob
        const result = await analyzeWebsite(job)
        await resultQueue.publish(JSON.stringify(result))
        await msg.ack()
    }
}

export default subscribe