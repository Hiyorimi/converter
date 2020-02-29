# Java experiments

Repo for various snippets testing.

Project can be built with [docker image](https://stackoverflow.com/questions/27767264/how-to-dockerize-maven-project-and-how-many-ways-to-accomplish-it):

```bash
docker build -t maven_demo .
docker run --rm -it maven_demo:latest
```

However, up-to-date version will fail, unless DB image is provided.