#!/bin/bash

mvn install:install-file -Dfile=./instantor-api-0.4.5.jar -DgroupId=com.instantor -DartifactId=instantor-api -Dversion=0.4.5 -Dpackaging=jar
