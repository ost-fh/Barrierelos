// jest.config.ts
import type {Config} from "@jest/types"

const config: Config.InitialOptions = {
    preset: "ts-jest",
    testEnvironment: "node",
    verbose: true,
    modulePathIgnorePatterns: ["<rootDir>/dist/"],
}
export default config
