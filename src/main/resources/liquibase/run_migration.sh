#!/bin/bash
JSON_DB=`echo $VCAP_SERVICES | jq '.["user-provided"][] | select(.name == "un-notification-db")' --raw-output`

if [ -z "$JSON_DB" ]
then
JSON_DB=`echo $VCAP_SERVICES | jq '.["azure-postgresqldb"][]' --raw-output`
fi

DB_USERNAME=`echo $JSON_DB | jq '.credentials.username' --raw-output`
DB_PASSWORD=`echo $JSON_DB | jq '.credentials.password' --raw-output`
DB_URL=`echo $JSON_DB | jq '.credentials.uri'  --raw-output| sed 's/postgres.*@/jdbc:postgresql:\/\//g'`

./.java-buildpack/open_jdk_jre/bin/java -cp ".:BOOT-INF/lib/*" liquibase.integration.commandline.Main \
  --changeLogFile=BOOT-INF/classes/liquibase/db.changelog-master.xml \
  --username=$DB_USERNAME \
  --password=$DB_PASSWORD \
  --liquibaseSchemaName="maintenance_alert" \
  --url=$DB_URL \
  --driver=org.postgresql.Driver \
  --classpath=".:BOOT-INF/classes/:`find BOOT-INF/lib/ -name postgresql-*.jar`" \
  --logLevel=DEBUG  \
  update