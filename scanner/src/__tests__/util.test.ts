import {describe, it} from "mocha";
import {expect} from "chai";
import {loadAppProperties} from "../util.js";

describe("subscribeToQueue()", () => {
    it("should check that the environment variable RABBITMQ_USER is set", () => {
        delete process.env.RABBITMQ_USER
        process.env.RABBITMQ_PASSWORD = "a"
        process.env.RABBITMQ_HOSTNAME = "a"
        process.env.RABBITMQ_PORT = "a"

        expect(loadAppProperties).to.throw(Error)
    })
    it("should check that the environment variable RABBITMQ_PASSWORD is set", () => {
        process.env.RABBITMQ_USER = "a"
        delete process.env.RABBITMQ_PASSWORD
        process.env.RABBITMQ_HOSTNAME = "a"
        process.env.RABBITMQ_PORT = "a"

        expect(loadAppProperties).to.throw(Error)
    })
    it("should check that the environment variable RABBITMQ_HOSTNAME is set", () => {
        process.env.RABBITMQ_USER = "a"
        process.env.RABBITMQ_PASSWORD = "a"
        delete process.env.RABBITMQ_HOSTNAME
        process.env.RABBITMQ_PORT = "a"

        expect(loadAppProperties).to.throw(Error)
    })
    it("should check that the environment variable RABBITMQ_PORT is set", () => {
        process.env.RABBITMQ_USER = "a"
        process.env.RABBITMQ_PASSWORD = "a"
        process.env.RABBITMQ_HOSTNAME = "a"
        delete process.env.RABBITMQ_PORT

        expect(loadAppProperties).to.throw(Error)
    })
})