FROM centos:7

ENV CASSANDRA_VERSION=3.11.2
ENV SPARK_VERSION=2.3.1
ENV CASSANDRA_HOME=/usr/lib/apache-cassandra-$CASSANDRA_VERSION
ENV SPARK_HOME=/usr/lib/spark-$SPARK_VERSION-bin-hadoop2.7
ENV PATH=$PATH:$CASSANDRA_HOME/bin:$CASSANDRA_HOME/tools/bin:$SPARK_HOME/bin:$SPARK_HOME/sbin

# Download Cassandra binary, create cassandra user & group
RUN yum -y install java initscripts sudo urw-fonts epel-release openssl-devel libxml2-devel curl-devel && \
    yum -y install R && yum clean all && \
    R -e 'install.packages(c("sparklyr","dplyr"), repos="http://cran.us.r-project.org")' && \
    curl -s https://bootstrap.pypa.io/get-pip.py | python && \
    pip install --upgrade pip jupyter findspark && \
    mkdir /usr/lib/jupyter && \
    rpm -ivh https://dl.influxdata.com/influxdb/releases/influxdb-1.6.2.x86_64.rpm && \
    rpm -ivh https://s3-us-west-2.amazonaws.com/grafana-releases/release/grafana-5.2.4-1.x86_64.rpm && \
    rpm -ivh https://dl.influxdata.com/telegraf/releases/telegraf-1.7.4-1.x86_64.rpm && \
    rpm -ivh http://central.maven.org/maven2/org/jmxtrans/jmxtrans/270/jmxtrans-270.rpm && \
    curl -# http://archive.apache.org/dist/cassandra/$CASSANDRA_VERSION/apache-cassandra-$CASSANDRA_VERSION-bin.tar.gz | tar zx -C /usr/lib && \
    curl -# https://archive.apache.org/dist/spark/spark-$SPARK_VERSION/spark-$SPARK_VERSION-bin-hadoop2.7.tgz | tar zx -C /usr/lib && \
    sed "s/log4j.rootCategory=.*/log4j.rootCategory=ERROR, console/g" $SPARK_HOME/conf/log4j.properties.template > $SPARK_HOME/conf/log4j.properties && \
    groupadd cassandra && \
    useradd -g cassandra cassandra

COPY files_to_copy/grafana* /tmp/
COPY files_to_copy/demo* /tmp/
COPY files_to_copy/telegraf* /etc/telegraf/
COPY files_to_copy/jmxtrans* /var/lib/jmxtrans/
COPY files_to_copy/cassandra* $CASSANDRA_HOME/conf/

# Attaching data volume
VOLUME $CASSANDRA_HOME/data

# Exposing required ports 3000(grafana) 7000(nossl) 7199(jmx) 8086(influxdb) 9042(client) spark ports 4040(pyspark) 4041(sparkR) 7077(job submission) 8080(master web) 8081(worker web) 8082(jupyter web)
EXPOSE 3000 7000 7199 8086 9042 4040 4041 7077 8080 8081 8082

# Entrypoint
COPY entrypoint.sh /tmp/entrypoint.sh
ENTRYPOINT /tmp/entrypoint.sh
