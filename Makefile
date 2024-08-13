COMPOSE_PROJECT_NAME=appbudget
COMPOSE=docker compose --project-name=$(COMPOSE_PROJECT_NAME) -f docker-compose.yml

.PHONY: upd
upd:
	$(COMPOSE) up -d

.PHONY: up
up:
	$(COMPOSE) up

.PHONY: down
down:
	$(COMPOSE) down

.PHONY: db
db:
	docker exec -it appbudget-mdb sh -c 'mariadb -uroot -prootpassword < /sql/schema.sql'

