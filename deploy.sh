#!/bin/bash

start=$(date +"%s")

scp -i key.txt -P ${SERVER_PORT} -o StrictHostKeyChecking=no ./docker-compose.yml ${SERVER_USER}@${SERVER_HOST}:/srv/pap/

ssh -p ${SERVER_PORT} ${SERVER_USER}@${SERVER_HOST} -i key.txt -t -t -o StrictHostKeyChecking=no << ENDSSH

cd /srv/pap
cat<<EOF > /srv/pap/docker-compose.deployment.yml
services:
  backend:
    command: "--spring.mail.password=${GMAIL_PASSWORD}"
EOF

docker compose -f docker-compose.yml -f docker-compose.deployment.yml up --pull=always -d

exit
ENDSSH

if [ $? -eq 0 ]; then
  exit 0
else
  exit 1
fi

end=$(date +"%s")

diff=$(($end - $start))

echo "Deployed in : ${diff}s"
