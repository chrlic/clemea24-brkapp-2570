FROM openjdk:14-alpine

ENV MVN_CENTRAL_URL https://repo1.maven.org/maven2
ENV HSQLDB_MVN_GRP org/hsqldb
ENV HSQLDB_VERSION 2.7.1

RUN apk update \                                                                                                                                                                                                                        
   && apk add ca-certificates wget openssl \                                                                                                                                                                                                      
   && update-ca-certificates \
   && mkdir -p /opt/hsqldb/lib \
   && wget -O /opt/hsqldb/lib/hsqldb.jar \
   "${MVN_CENTRAL_URL}/${HSQLDB_MVN_GRP}/hsqldb/${HSQLDB_VERSION}/hsqldb-${HSQLDB_VERSION}.jar" \
   && wget -O /opt/hsqldb/lib/sqltool.jar \
   "${MVN_CENTRAL_URL}/${HSQLDB_MVN_GRP}/sqltool/${HSQLDB_VERSION}/sqltool-${HSQLDB_VERSION}.jar" \
   && mkdir -p /etc/opt/hsqldb/conf \
   && mkdir -p /var/opt/hsqldb/sql \
   && mkdir -p /var/opt/hsqldb/data \
   && mkdir -p /var/opt/hsqldb/run \
   && addgroup -S hsqldb \
   && adduser -S -g hsqldb hsqldb 

COPY conf/ /etc/opt/hsqldb/conf/

RUN chown hsqldb:hsqldb -R /opt/hsqldb \
   && chown hsqldb:hsqldb -R /var/opt/hsqldb \
   && chown hsqldb:hsqldb -R /etc/opt/hsqldb

EXPOSE 9001
USER hsqldb
CMD java \
   -cp /opt/hsqldb/lib/hsqldb.jar \
   -Djava.util.logging.config.file=/etc/opt/hsqldb/conf/logging.properties \
   org.hsqldb.server.Server --props \
   /etc/opt/hsqldb/conf/server.properties