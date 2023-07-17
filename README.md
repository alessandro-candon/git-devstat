# Getting Started

## Commands

Format code:
```shell
./gradlew spotlessApply
```

Build and exec:
```shell
./gradlew build
java -jar build/libs/gitdevstat-0.0.1-SNAPSHOT.jar
```

Native build and exec (set GraalVM 22.3.2 Java 17 CE):
```shell
./gradlew nativeCompile
./build/native/nativeCompile/gitdevstat
```

## Run job

Setup a .env.local like the .env with your personal environment and secrets and run command

```shell
source .env.local
```

In this way you have all the variables to run your script.

Some example call:
```
analyze Pub,Pub,Pub git-devstat,SpringMicroservice,marketdata-api-js alessandro-candon/git-devstat,cmauri75/SpringMicroservice,barchart/marketdata-api-js
single-analysis Pub git-devstat alessandro-candon/git-devstat
single-analysis Pub SpringMicroservice cmauri75/SpringMicroservice
single-analysis Pub marketdata-api-js barchart/marketdata-api-js
```
