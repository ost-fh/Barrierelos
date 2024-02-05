import "dotenv/config"
import subscribe from "./rabbitmq.js";
import Logger from "./logger.js";
import {loadAppProperties} from "./util.js";

async function subscribeToQueue() {
    const appProperties = loadAppProperties()
    try {
        await subscribe(appProperties)
    } catch (e) {
        Logger.error(e)
        Logger.info("Reconnecting to RabbitMQ...")
        setTimeout(subscribeToQueue, 3000)
    }
}

await subscribeToQueue()
