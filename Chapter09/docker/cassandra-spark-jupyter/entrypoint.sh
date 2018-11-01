#!/usr/bin/env bash
set -e

[ -z $CASSANDRA ] && CASSANDRA=true
[ -z $MONITOR ] && MONITOR=false
[ -z $SPARK ] && SPARK=true
[ -z $SPARK_CLI ] && SPARK_CLI=pyspark
[ -z $CS_CONSISTENCY ] && CS_CONSISTENCY=LOCAL_ONE

# Traping signal from pod
trap 'exit' TERM SIGTERM QUIT SIGQUIT INT SIGINT KILL SIGKILL

__start(){
  if [ "$CASSANDRA" == 'true' ]; then
    if [ "$MONITOR" == 'true' ]; then
      influxd >> /var/log/influxdb/influxdb.log 2>> /var/log/influxdb/influxdb.log &
      service grafana-server start && service telegraf start && service jmxtrans start
      curl -XPOST "http://localhost:8086/query" --data-urlencode "q=CREATE DATABASE telegraf"
      curl -XPOST "http://localhost:3000/api/datasources" -H "Content-Type: application/json" --user admin:admin --data-binary @/tmp/grafanaDataSource.json
      curl -XPOST "http://localhost:3000/api/dashboards/db" -H "Content-Type: application/json" --user admin:admin --data-binary @/tmp/grafanaDashboard.json
    fi
    chown -R cassandra:cassandra $CASSANDRA_HOME
    su -m cassandra -c "cassandra >> $CASSANDRA_HOME/system.log 2>> $CASSANDRA_HOME/debug.log"
  fi
  if [ "$SPARK" == 'true' ]; then
    EXTRA_PARAMETERS=''
    #Setting Authentication parameters if they exists
    if [ ! -z $CS_HOST ] && [ ! -z $CS_DC ]; then
      if [ ! -z $CS_UNAME ]; then
        if [ ! -z $CS_PWD ]; then
          EXTRA_PARAMETERS+="--conf spark.cassandra.auth.username=$CS_UNAME --conf spark.cassandra.auth.password=$CS_PWD "
        else
          echo 'CS_UNAME and CS_PWD are mandatory for successful authentication, if authentication is not enabled on Cassandra then remove from env while running docker container else please set them'
          exit
        fi
      fi
      EXTRA_PARAMETERS+="--conf spark.cassandra.input.consistency.level=$CS_CONSISTENCY "
      if [ ! -z $TRUSTSTORE_PATH ]; then
        if [ ! -z $TRUSTSTORE_PWD ]; then
          EXTRA_PARAMETERS+="--conf spark.cassandra.connection.ssl.enabled=true --conf spark.cassandra.connection.ssl.trustStore.path=$TRUSTSTORE_PATH --conf spark.cassandra.connection.ssl.trustStore.password=$TRUSTSTORE_PWD"
        else
          echo 'TRUSTSTORE_PATH and TRUSTSTORE_PATH are mandatory, for successful ssl connection hence please set them'
          exit 1
        fi
      fi
    else
      echo 'CS_HOST and CS_DC are mandatory, hence please set them'
      exit 1
    fi
    start-master.sh -h 127.0.0.1
    start-slave.sh -h 127.0.0.1 spark://127.0.0.1:7077
    SPARK_PARAMETERS="--packages com.datastax.spark:spark-cassandra-connector_2.11:2.3.0 --master spark://127.0.0.1:7077 --conf spark.driver.host=127.0.0.1 --conf spark.cassandra.connection.host=$CS_HOST --conf spark.cassandra.connection.local_dc=$CS_DC $EXTRA_PARAMETERS"
    if [ "$SPARK_CLI" == 'pyspark' ]; then
      pyspark $SPARK_PARAMETERS
    elif [ "$SPARK_CLI" == 'jupyter' ]; then
      jupyter notebook  --no-browser --port 8082 --allow-root --ip=0.0.0.0 --NotebookApp.token='' --notebook-dir=/usr/lib/jupyter 2>&1
    elif [ "$SPARK_CLI" == 'sparkR' ]; then
      sparkR
    fi
  fi
  /bin/bash
}

__load_data(){
  while : ; do
    if cqlsh -u cassandra -p cassandra -e 'LIST ROLES;' > /dev/null 2>&1; then
      exec cqlsh -u cassandra -p cassandra -f /tmp/demo.cql
      break
    else
      sleep 10
    fi
  done &
}

__load_data
__start || echo "======== Foreground processes returned code: '$?'"
