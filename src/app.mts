import "dotenv/config"
import subscribe from "./rabbitmq.mjs";

async function subscribeToQueue() {
    try {
        await subscribe()
    } catch (e) {
        console.error(e)
        console.log("Reconnecting to RabbitMQ...")
        setTimeout(subscribeToQueue, 3000)
    }
}

await subscribeToQueue()
