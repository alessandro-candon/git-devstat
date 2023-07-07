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

