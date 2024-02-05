/* eslint-disable @typescript-eslint/no-unsafe-argument */
import {beforeEach, describe, it} from "mocha"
import * as td from "testdouble"
import {AppProperties} from "../model.js";
import logger from "../logger.js";
import subscribe from "../rabbitmq.js";
import {AMQPChannel, AMQPClient, AMQPConsumer, AMQPQueue} from "@cloudamqp/amqp-client";
import {AMQPBaseClient} from "@cloudamqp/amqp-client/amqp-base-client";


before(() => {
    logger.transports.forEach(value => value.silent = true)
})
describe("subscribe()", () => {
    let props: AppProperties
    beforeEach(() => {
        props = {
            rabbitmqPort: "1",
            rabbitmqHostname: "example.com",
            rabbitmqPassword: "a",
            rabbitmqUser: "a",
        }

        td.reset()
        const connect = td.replace(AMQPClient.prototype, "connect")
        const connection = td.object<AMQPBaseClient>()
        td.when(connect()).thenResolve(connection)
        const channel = td.object<AMQPChannel>()
        td.when(connection.channel()).thenResolve(channel)
        const queue = td.object<AMQPQueue>()
        td.when(channel.queue(td.matchers.anything())).thenResolve(queue)
        const consumer = td.object<AMQPConsumer>()
        td.when(queue.subscribe(td.matchers.anything(), td.matchers.anything())).thenResolve(consumer)
    })
    it("Should create all required RabbitMQ instances", async () => {
        await subscribe(props)
    })
});

