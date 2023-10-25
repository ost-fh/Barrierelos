import {AMQPClient, AMQPMessage} from "@cloudamqp/amqp-client";


async function subscribe() {
    const amqp = new AMQPClient(process.env.RABBIT_MQ_URL ?? "amqp://localhost")
    const conn = await amqp.connect()
    const ch = await conn.channel()
    const jobQueue = await ch.queue("analysis_job_queue")
    const resultQueue = await ch.queue("analysis_result_queue")
    const consumer = await jobQueue.subscribe({exclusive: true, noAck: false}, handleMessage)

    console.log("Connected to RabbitMQ")

    await consumer.wait()
    await conn.close()

    function handleMessage(msg: AMQPMessage) {
        console.log(msg.bodyToString())
        void (async () => {
            const result = "test"
            await resultQueue.publish(JSON.stringify(result))
            await msg.ack()
        })();
    }
}

export default subscribe