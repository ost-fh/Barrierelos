import "dotenv/config"
import subscribe from "./rabbitmq.js";
import Logger from "./logger.js";

async function subscribeToQueue() {
    try {
        await subscribe()
    } catch (e) {
        Logger.error(e)
        Logger.info("Reconnecting to RabbitMQ...")
        setTimeout(subscribeToQueue, 3000)
    }
}

await subscribeToQueue()
