# Barrierelos Deployment

This repo includes the deployment configuration for the Barrierelos project.
That includes both the logic to deploy to the server and to run specific components locally in Docker.
Running specific components locally in Docker is useful for development when you want to work on a single component and
start the other components locally in Docker. We were developing on Linux and as such, some scripts might not work on
Windows or macOS. MacOS should technically work, but we didn't test it.

If you're interested in the architecture of the Barrierelos project, decision records, and other documentation, please
refer to the [project documentation](http://barrierelos.pages.gitlab.ost.ch/documentation/index.html) on GitLab Pages.

## Repositories

The Barrierelos project consists of the three primary repos:

- [scanner](https://gitlab.ost.ch/barrierelos/scanner):
  The Typescript / Node.js application that runs the Axe-core a11y scan jobs submitted by the backend via RabbitMQ.
- [backend](https://gitlab.ost.ch/barrierelos/backend):
  The Kotlin / Spring backend API used by the frontend, which also submits Axe-core a11y scan jobs to the scanner via
  RabbitMQ.
- [frontend](https://gitlab.ost.ch/barrierelos/frontend):
  The Typescript / React frontend which uses client-side-rendering (CSR).

The Barrierelos project also includes the following additional repos:

- [documentation](https://gitlab.ost.ch/barrierelos/documentation):
  The ReStructuredText / Sphinx documentation for the Barrierelos project which is published to GitLab Pages.
- [tools/scripts](https://gitlab.ost.ch/barrierelos/tools/scripts):
  Contains some helper scripts to add example websites via the backend API and to scrape information we needed while
  developing the project.
- [tools/bruno](https://gitlab.ost.ch/barrierelos/tools/bruno):
  Contains the [Bruno](https://www.usebruno.com/) configuration. Bruno is an open-source API tool similar to Postman.

## File descriptions

- `.gitlab-ci-docker-publish-deploy.yml`:
  Includes the shared GitLab CI logic to build a Docker image and publish it to the GitLab registry.
  This file is included in the `gitlab-ci.yml` files of the other repos.
- `compose.yml`:
  The Docker Compose file to deploy the Barrierelos project on the server.
- `compose-dev.yml`:
  This Docker Compose file provides overrides for the `compose.yml` to run the Barrierelos project locally in Docker.
- `Makefile`:
  This Makefile provides commands to simplify running the Barrierelos project locally in Docker.
- `.env`:
  This file contains the [environment variables](#environment-variables) to configure how the Barrierelos project is run
  locally in Docker.

## Environment Variables

The environment variables in `.env` are used to configure how the Barrierelos project is run locally in Docker.

- `CONTAINER_REGISTRY_TOKEN`:
  The token to log in to the GitLab container registry via `make docker-login`.
  A new Group Access Token for this purpose can be
  created [here](https://gitlab.ost.ch/groups/barrierelos/-/settings/access_tokens).
- `DOCKER_SERVICES`:
  The Docker services to run locally in Docker via `make docker-up`.
  If you want to work on the backend for example, you can remove the `backend` from the list so that you can launch it
  locally outside of Docker in your IDE.

The other environment variables are used in the Docker Compose files to configure credentials and ports of the services.

## Run the project locally in Docker

To run the Barrierelos project locally in Docker, you need to have Docker and [make](https://www.gnu.org/software/make/)
installed.

> Make sure that you have configured [environment variables](#environment-variables) in the `.env` file as desired
> before
> running the commands below.

In order for the Docker Compose file to access the images in the GitLab registry,
you need to log in to the GitLab registry. This step is only required once.

> Note: If you have never logged in to a Docker registry before, you may first need to configure a Docker
> [credential store](https://docs.docker.com/engine/reference/commandline/login/#credential-stores).

```bash
make docker-login
```

To pull the latest images from the GitLab registry, run:

```bash
make docker-pull
```

> Note: This will only pull the images that are listed in the `DOCKER_SERVICES` environment variable.
> This is useful for example if you don't want to pull the most recent `scanner` image yet because your
> `backend` branch is not compatible with it yet.

To start the Barrierelos project locally in Docker, run:

```bash
make docker-up
```

To stop the Barrierelos project locally in Docker, run:

> Note: This command will also remove all containers, networks, volumes, and images created by `make docker-up`.
> This includes the database and all data stored in it.

```bash
make docker-down
```

## Accessing the RabbitMQ web interface

After starting the `rabbitmq` service locally in Docker, you can access the RabbitMQ web interface at the following URL:
http://localhost:15672/. The default username and password are both `barrierelos`.

The `scanner` and the `backend` will automatically create the required queues and exchanges.
You can use the web interface to manually insert website scan jobs or results into the queues. As well as to inspect the
messages in the queues. And lastly you can purge all messages in the queues in case some of them remained there during
development.

## Accessing the PostgreSQL database

The [backend](https://gitlab.ost.ch/barrierelos/backend) project pre-configured data sources to access the local
database and the one on the server. This is documented
[here](https://gitlab.ost.ch/barrierelos/backend/-/blob/main/README.md#accessing-the-postgresql-database).

## GitLab CI/CD Pipeline

Whenever you push a commit to any branch of one of the primary [repositories](#repositories), a GitLab CI pipeline is
triggered. Each pipeline consists of multiple jobs that accomplish the same task but for the different technologies used
in these components. The procedure goes as follows:

- Build the application
- Test / lint the application
- Build a Docker image and publish it to the GitLab registry
- Deploy the image to the server

The first two steps are implemented in the `.gitlab-ci.yml` file of the corresponding repository. The last two steps are
shared between all primary repositories and is therefore implemented in this repository, in the
`.gitlab-ci-docker-publish-deploy.yml` file. The two last steps are only executed automatically when pushing to
the `main` branch. When pushing a commit to another branch the auto-build and -deploy jobs are still available but must
be triggered manually, since commits to feature branches should not be deployed to the server automatically.

Since the different primary repos depend on each other, you can also disable the auto-build and -deploy pipelines on
the `main` branch by including the string `no_auto_deploy` in the commit message. This way you can for example finish
merging all merge requests in different repos before triggering the auto-build and -deploy jobs.

The `documentation` repo also has an auto-build and -deploy pipeline, which functions similar to the pipelines described
above. It builds the documentation as HTML and PDF and publishes it to GitLab Pages.
