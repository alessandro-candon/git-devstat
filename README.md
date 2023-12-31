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
analyze false,false,false git-devstat,SpringMicroservice,marketdata-api-js alessandro-candon/git-devstat,cmauri75/SpringMicroservice,barchart/marketdata-api-js
single-analysis false git-devstat alessandro-candon/git-devstat
single-analysis false SpringMicroservice cmauri75/SpringMicroservice
single-analysis false marketdata-api-js barchart/marketdata-api-js
single-analysis true osm-proxy asd/osm-proxy
```

### analyze-from-config
Notes for developer: 
* ``analyze-from-config`` command reads config and analyze all repos referring to a list of teams.
    No paramaters are needed, just add application-XXX.yaml as example and run with profile XXX

* add ``logging.level.org.devstat.gitdevstat.view.linesofcodebyauthor=TRACE`` to application.yaml or to runtime env in order to trace all checked files and verify if they are filtered
```
app:
  github:
    teams: [ "tacos" ]
  config:
      authorIds:
        alessandro-candon:
            - 44228481+alessandro-candon@users.noreply.github.com
            - alessandro.candon@asd.com
            - alexcandy91@hotmail.it
        pasquale-martucci:
            - 44227883+pmartu12@users.noreply.github.com
            - pasqualemartucci@gmail.com
            - pasquale.martucci@asd.com
            - pasquale.martucci@asd.com
      excludedFiles:
          - composer.lock
          - package.json
          - package-lock.json
          - .idea.*
          - ^bin/.*
          - ^classes/.*
          - .*.build.*
          - docapi.*
          - phpunit.phar
          - phpunit.xml.dist
          - symfony.lock
      timeFrameDto:
        from: "2023-01-01"
        to: "2024-01-01"
```

Stat command is:
```
git log --pretty=format:"commit %h|%an|%ae|%al|%aD|%at|%cn|%ce|%cD|%ct|%f" --numstat --after=2022-12-31 --before=2024-01-01
```
