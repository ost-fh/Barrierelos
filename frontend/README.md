# Barrierelos Frontend

This is the repository for the Barrierelos frontend which provides the web interface for the Barrierelos project. It
accesses the [backend](https://gitlab.ost.ch/barrierelos/backend) API to retrieve its data and orchestrates the
authentication workflows with `username + password` or `OAuth2` using Google. It is written in TypeScript / React and
uses the Material UI component library. Vite is used as the build tool.

## Directory Structure

The project was set up with Vite and follows its directory structure.

- `public` contains static files that are not directly referenced in the code. These are copied to the `dist`
  directory during the build.
    - `locales` contains the i18next translation files.
- `src` contains the source code of the frontend.
    - `@types` contains TypeScript helper type definitions for the project. For example for the translation files.
    - `assets` contains static files that are directly referenced in the code. Further explanation of the difference
      between the `assets` and the `public` folder are
      documented [here](https://vitejs.dev/guide/assets#the-public-directory).
    - `components` contains the React components that are used throughout the project.
    - `context` contains the React contexts that are used throughout the project.
    - `lib` contains automatically generated code, like the OpenAPI client for example. This folder is listed in
      the `.gitignore` file.
    - `model` contains the data models for the frontend. Whenever possible, we used the models from the OpenAPI client
      instead of defining our own models.
    - `pages` contains the different webpages of the application. Whenever a page consisted of multiple files, we
      created a subfolder for it.
    - `services` contains the services that are used throughout the project. These are to access the backend API when
      the OpenAPI client was not sufficient.
    - `util` contains utility functions that are used throughout the project.

## Files

- `src`
    - `App.css` contains the global CSS styles for the application.
    - `App.tsx` is the main React component of the application. It contains the routing logic, theme and the global
      state
      providers.
    - `main.tsx` is the entry point of the React application. It renders the `App` component into the `#root` element of
      the `index.html` file.
    - `i18n.ts` contains the configuration for the i18next translation library.
    - `vite-env.d.ts` contains the type definitions for the Vite build tool. Despite looking quite empty the first
      line is actually required to import SVGs.
- `.gitlab-ci.yml` contains the GitLab CI/CD pipeline configuration. It is used to automatically build the
  application on the GitLab CI/CD runners. It also contains an `include` for the shared Docker image build, publish and
  deployments from the [deployment](https://gitlab.ost.ch/barrierelos/deployment) repository.
- `.eslintrc.js` contains the ESLint configuration.
- `Dockerfile` contains the logic to build the Docker image which is used by the GitLab CI/CD pipeline.
- `index.html` is the HTML template for the application. It contains the `#root` element into which the React
  application is rendered.
- `openapi.json` contains the OpenAPI specification for the backend API. It is fetched from the backend running locally
  or on the server with the corresponding [npm scripts](#npm-scripts).
- `package.json` contains the npm configuration. Also contains some additional scripts described in
  the [npm scripts](#npm-scripts) section.
- `tsconfig.json` contains the TypeScript configuration for local development.
- `tsconfig.node.json` contains the TypeScript configuration for the production build.

## Development

Before getting started, have a look at the [deployment](https://gitlab.ost.ch/barrierelos/deployment) repository where
you will learn how to set up the development environment for the project overall. This sections will only
describe the steps that are specific to the scanner.

We developed the frontend with WebStorm, and we highly recommend using it for development since our development
workflow is well integrated into the IDE. When you open the project in WebStorm, everything should be set up configured
correctly. A popup should appear asking you to install the dependencies. If it doesn't, you can also run `npm install`.

We were developing on Linux and as such, some scripts might not work on Windows or macOS. MacOS should
technically work, but we didn't test it.

The project already comes with two run configurations:

> Before executing these running configuration, make sure you started the required components as per the instructions
> in the [deployment](https://gitlab.ost.ch/barrierelos/deployment) repository.

Before running the application locally, you need to fetch the OpenAPI specification for the local environment from the
backend. So make sure the backend is running locally and then run the `npm run openapi:fetch` script.

- `Run Locally` to run the application locally. It should only be started normally not in debug mode since this doesn't
  change anything. If you need to debug, have a look at the `Debug` run configuration.
- `Debug` to open a new browser window which is connected to the WebStorm debugger. This way you can set breakpoints in
  the code within the IDE just like normal. This would not work otherwise since we're using client side rendering (CSR).
  Make sure you started the `Run Locally` run configuration before starting this one.

Before committing any changes, have a look at the [OpenAPI](#openapi) section.

### npm Scripts

The following npm scripts are available:

- `npm run dev` used by the `Run Locally` run configuration to start the application locally.
- `npm run openapi:fetch` used to fetch the OpenAPI specification from the backend running locally. More information
  about this can be found in the [OpenAPI](#openapi) section.
- `npm run openapi:fetch:prod` used to fetch the OpenAPI specification from the backend running on the server. More
  information about this can be found in the [OpenAPI](#openapi) section.
- `npm run openapi:generate` used to generate the OpenAPI client from the OpenAPI specification. More information about
  this can be found in the [OpenAPI](#openapi) section.
- `npm run i18n` used to generate the TypeScript types for the translation files. This is automatically executed when
  running the `npm run build` script or when the `Run Locally` run configuration is started. So when you added new
  entries to the translation files, you can either restart the `Run Locally` run configuration or run this script.
- `npm run test:lint` used by the GitLab CI/CD pipeline to lint the code.
- `npm run test:typecheck` used by the GitLab CI/CD pipeline to type check the code.

## OpenAPI

The `openapi.json` file contains the OpenAPI specification for the backend API.

When you are developing, you will probably want to use the OpenAPI specification pointing to the backend running
locally. To fetch that OpenAPI specification, run the `npm run openapi:fetch` script. This overwrites the existing file
however, so make sure you're not committing it.

Whenever the backend API has changed, and you want to update the OpenAPI specification used by the frontend running on
the server, you need to fetch the corresponding OpenAPI specification from the backend with
the `npm run openapi:fetch:prod` script and then add it to your commit.

To continue developing after this, you will want to switch back to the OpenAPI specification pointing to the backend
running locally as described above.

Note that the two npm scripts above will only fetch the OpenAPI specification not generate the OpenAPI client. The
OpenAPI client is generated automatically when running the `Run Locally` run configuration. Or you can generate it
manually with the `npm run openapi:generate` script. The generated code is located in the `src/lib` folder and is
listed in the `.gitignore` file so that it is not committed. You don't have to worry about the client being generated on
the server since it is automatically generated when the GitLab CI/CD pipeline builds the application
with `npm run build`.
