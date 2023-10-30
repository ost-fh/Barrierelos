import {AMQPClient, AMQPMessage} from "@cloudamqp/amqp-client";
import {AnalysisJob} from "./model.js";
import {analyzeWebsite} from "./scanner.js";
import {formatFailedAnalysisResult} from "./formatter.js";

export async function subscribe() {
    const amqpClientFactory = new AMQPClient(process.env.RABBIT_MQ_URL ?? "amqp://localhost")
    const amqpClient = await amqpClientFactory.connect()
    const channel = await amqpClient.channel()
    const jobQueue = await channel.queue("analysis_job_queue")
    const resultQueue = await channel.queue("analysis_result_queue")
    const consumer = await jobQueue.subscribe({exclusive: true, noAck: false}, handleMessage)

    console.log("Connected to RabbitMQ")

    await consumer.wait()
    await amqpClient.close()

    async function handleMessage(msg: AMQPMessage) {
        if (!msg.bodyString()) {
            const errorMessage = "Received a message without a body"
            console.error(errorMessage)
            const failResult = formatFailedAnalysisResult("unknown", errorMessage)
            await resultQueue.publish(JSON.stringify(failResult))
            await msg.reject(false)
            return
        }

        const job: AnalysisJob = JSON.parse(msg.bodyString() as string)
        const result = await analyzeWebsite(job)
        await resultQueue.publish(JSON.stringify(result))
        await msg.ack()
    }
}

export default subscribe