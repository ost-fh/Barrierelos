# Barrierelos Backend

## Documentation

### OpenAPI

An OpenAPI 3 documentation can be found at:

* [JSON](http://localhost:8030/openapi)
* [YAML](http://localhost:8030/openapi.yaml)
* [Swagger](http://localhost:8030/swagger/index.html)

```
http://host:port/openapi             (JSON)
http://host:port/openapi.yaml        (YAML)
http://host:port/swagger/index.html  (Swagger)
```

## Setup

1. [Create new GPG key](https://docs.github.com/en/authentication/managing-commit-signature-verification/generating-a-new-gpg-key)

```shell
gpg -k
```

2. [Initialize pass store](https://manpages.ubuntu.com/manpages/trusty/man1/pass.1.html)

```shell
pass init NAME_OF_GPG_ID
```

3. Login to Docker registry

```shell
echo "CONTAINER_REGISTRY_TOKEN" | docker login registry.gitlab.ost.ch:45023 -u doesntmatter --password-stdin
```
