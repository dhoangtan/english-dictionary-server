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
___
Docker Composing Step
Step one: Remove all image and volumes in of the projects 
```shell
docker-compose down --rmi all -- volumes
```
Step two: build the docker image with-out any cache (explain later)
```shell
docker-compose build --no-cache 
```
Step three: run the docker image
```shell
docker-compose up
```
Return to Step One
=======

