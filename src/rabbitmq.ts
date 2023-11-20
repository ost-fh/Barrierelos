import {AMQPClient, AMQPMessage} from "@cloudamqp/amqp-client";
import {AnalysisJob} from "./model.js";
import {analyzeWebsite} from "./scanner.js";
import {formatFailedAnalysisResult} from "./formatter.js";
import Logger from "./logger.js";

export async function subscribe() {
    const user = process.env.RABBITMQ_USER ?? "guest"
    const password = process.env.RABBITMQ_PASSWORD ?? "guest"
    const hostname = process.env.RABBITMQ_HOSTNAME ?? "localhost"
    const url = `amqp://${user}:${password}@${hostname}`
    const amqpClient = new AMQPClient(url)
    const connection = await amqpClient.connect()
    const channel = await connection.channel()
    const jobQueue = await channel.queue("barrierelos.analysis.job")
    const resultQueue = await channel.queue("barrierelos.analysis.result")
    const consumer = await jobQueue.subscribe({exclusive: true, noAck: false}, handleMessage)

    Logger.info(`Connected to RabbitMQ at ${url}`)

    await consumer.wait()
    await connection.close()

    async function handleMessage(msg: AMQPMessage) {
        const bodyString = msg.bodyString()
        if (bodyString === null) {
            const errorMessage = "Received a message without a body"
            Logger.error(errorMessage)
            const failResult = formatFailedAnalysisResult("unknown", errorMessage)
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