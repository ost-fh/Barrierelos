docker-login:
	(. $(PWD)/.env ; echo "$$CONTAINER_REGISTRY_TOKEN" | docker login registry.gitlab.ost.ch:45023 -u doesntmatter --password-stdin)

docker-pull:
	(. $(PWD)/.env ; docker compose -f compose.yml -f compose-dev.yml pull $$DOCKER_SERVICES)

docker-up:
	(. $(PWD)/.env ; docker compose -f compose.yml -f compose-dev.yml up -d $$DOCKER_SERVICES)

docker-down:
	docker compose -f compose.yml -f compose-dev.yml down
