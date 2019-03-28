#!/bin/sh
mvn -Dmaven.test.skip=true package &&
docker build --tag isitraining . &&
docker rm isitraining &&
docker run --net=letsencrypt_default --name=isitraining -dt isitraining
