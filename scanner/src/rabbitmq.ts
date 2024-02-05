import {AMQPClient, AMQPMessage} from "@cloudamqp/amqp-client";
import {AppProperties, ScanJob} from "./model.js";
import {scanWebsite} from "./scanner.js";
import {formatFailedWebsiteResult} from "./formatter.js";
import Logger from "./logger.js";


const JOB_QUEUE_NAME = "barrierelos.scan.job"
const RESULT_QUEUE_NAME = "barrierelos.scan.result"

export async function subscribe(props: AppProperties) {
    const user = encodeURIComponent(props.rabbitmqUser)
    const password = encodeURIComponent(props.rabbitmqPassword)
    const hostname = encodeURI(props.rabbitmqHostname)
    const port = props.rabbitmqPort
    const url = `amqp://${user}:${password}@${hostname}:${port}`

    const logUrl = `amqp://${user}:<password>@${hostname}:${port}`
    Logger.info(`Attempting to connect to RabbitMQ at ${logUrl}`)
    const amqpClient = new AMQPClient(url)
    const connection = await amqpClient.connect()
    Logger.info(`Connected to RabbitMQ`)

    const channel = await connection.channel()
    await channel.basicQos(1)
    const jobQueue = await channel.queue(JOB_QUEUE_NAME)
    const resultQueue = await channel.queue(RESULT_QUEUE_NAME)
    const consumer = await jobQueue.subscribe({exclusive: true, noAck: false}, handleMessage)

    // Wait for the client to close the connection which should never happen unless an error occurs
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

        const job: ScanJob = JSON.parse(bodyString) as ScanJob
        const websiteResult = await scanWebsite(job)
        await resultQueue.publish(JSON.stringify(websiteResult))
        await msg.ack()
    }
}

export default subscribe
