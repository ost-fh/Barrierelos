# Barrierelos Scanner

[![pipeline status](https://gitlab.ost.ch/barrierelos/scanner/badges/main/pipeline.svg)](https://gitlab.ost.ch/barrierelos/scanner/-/commits/main)
[![coverage report](https://gitlab.ost.ch/barrierelos/scanner/badges/main/coverage.svg)](https://gitlab.ost.ch/barrierelos/scanner/-/commits/main)

This is the repository for the Barrierelos scanner which is responsible for the automated website accessibility scan
using the Axe-core accessibility engine. The scan jobs are retrieved from the RabbitMQ queue and the results are pushed
back to another queue to be processed by the [backend](https://gitlab.ost.ch/barrierelos/backend).
It is written in TypeScript / Node.js and was designed to be as simple as possible in order for it to be replaceable
by another scanning component if the need arises. Therefore, it only scans a website and transforms the results into a
JSON format that is compatible with the [backend](https://gitlab.ost.ch/barrierelos/backend), without any further
processing.

## Directory Structure

- `src` contains the source code of the scanner.
  - `__tests__` contains the tests for the scanner.
  - `__mocks__` contains the mocks for the tests, i.e. it populates fake data for testing purposes.

## Files

- All the files in the root directory of `src` contain the actual implementation of the scanner.
  - `app.ts` is the entry point of the Node.js application and connects to the RabbitMQ server listening for scan
    jobs.
  - `rabbitmq.ts` contains the RabbitMQ connection and the functions to send and receive messages.
  - `scanner.ts` contains the actual scanning logic using the Axe-core accessibility engine. This includes a lot of
    logic to handle network time-outs and other errors that can occur during the scan.
  - `models.ts` contains the data models for the scan jobs and results.
  - `formatter.ts` contains the logic to transform the scan results from the Axe-core format into the format that is
    compatible with the [backend](https://gitlab.ost.ch/barrierelos/backend).
  - `utils.ts` contains utility functions that are used throughout the scanner.
- `.env` contains the environment variables that are used to connect to RabbitMQ. These only need to be adjusted if you
  changed the setup for local development in the [deployment](https://gitlab.ost.ch/barrierelos/deployment) repository.
- `.gitlab-ci.yml` contains the GitLab CI/CD pipeline configuration. It is used to automatically build and test the
  application on the GitLab CI/CD runners. It also contains an `include` for the shared Docker image build, publish and
  deployments from the [deployment](https://gitlab.ost.ch/barrierelos/deployment) repository.
- `.eslintrc.js` contains the ESLint configuration.
- `.mocharc.cjs` contains the Mocha configuration.
- `.c8rc.json` contains the c8 configuration.
- `Dockerfile` contains the logic to build the Docker image which is used by the GitLab CI/CD pipeline.
- `package.json` contains the npm configuration. Also contains some additional scripts described in
  the [npm scripts](#npm-scripts) section.
- `tsconfig.json` contains the TypeScript configuration for local development.
- `tsconfig.build.json` extends `tsconfig.json` with additional configuration for the production build.

## Development

Before getting started, have a look at the [deployment](https://gitlab.ost.ch/barrierelos/deployment) repository where
you will learn how to set up the development environment for the project overall. This sections will only
describe the steps that are specific to the scanner.

We developed the scanner with WebStorm, and we highly recommend using it for development since our development
workflow is well integrated into the IDE. When you open the project in WebStorm, everything should be set up configured
correctly. A popup should appear asking you to install the dependencies. If it doesn't, you can also run `npm install`.

We were developing on Linux and as such, some scripts might not work on Windows or macOS. MacOS should
technically work, but we didn't test it.

The project already comes with three run configurations:

> Before executing these running configuration, make sure you started the required components as per the instructions
> in the [deployment](https://gitlab.ost.ch/barrierelos/deployment) repository.

- `Run Locally` to run the application locally. It can either be started normally or in debug mode.
  It will connect to the RabbitMQ running locally in Docker directly after starting up.
- `Run All Tests` to run all tests with Mocha. The tests can also either be run normally or in debug mode.
- `Run All Tests with Coverage` to run all tests including coverage with c8. This can also either be run normally or in
  debug mode. Since there are compatability issues with Mocha, coverage and ES modules, we had to use c8 instead of
  Mocha's built-in coverage. WebStorm unfortunately doesn't support c8, but the coverage report is both printed to the
  console and picked up by GitLab CI/CD to show the coverage percentage on top of this README.

### npm Scripts

The following npm scripts are available:

- `npm run start:dev` used by the `Run Locally` run configuration to start the application locally.
- `npm run start` used as the entry point for the Docker image.
- `npm run test:lint` used by the GitLab CI/CD pipeline to lint the code.
- `npm run test:typecheck` used by the GitLab CI/CD pipeline to type check the code.
- `npm run test:unittest` used by the GitLab CI/CD pipeline to run the tests with coverage.
