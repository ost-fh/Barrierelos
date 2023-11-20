update-compose:
	git clone ssh://git@gitlab.ost.ch:45022/barrierelos/deployment.git
	cp ./deployment/compose.yml ./compose-prod.yml
	cp ./deployment/compose-dev.yml ./
	rm -rf ./deployment/

update-images:
	docker compose -f compose-prod.yml -f compose-dev.yml pull
