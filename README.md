# Run project on Docker
___
Package the project into .jar file
```shell
./mvnw package
```
Build docker image
```shell
docker build . -t server:lastest
```
Run docker container
```shell
docker run -d -p 4040:8080 server:lastest
```


