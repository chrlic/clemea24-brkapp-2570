# Pull base image.
FROM quay.io/centos/centos:stream8

# Install Java.
RUN yum update -y 
RUN yum install -y java-11-openjdk-devel maven
# RUN export PATH=/usr/lib/jvm/java-11-openjdk/bin/:$PATH
# RUN export JAVA_HOME=/usr/lib/jvm/jre-11-openjdk

RUN mkdir /cmd
COPY . /cmd
WORKDIR /cmd

# Define commonly used JAVA_HOME variable
ENV PATH=/usr/lib/jvm/java-11-openjdk/bin/:$PATH
ENV JAVA_HOME /usr/lib/jvm/jre-11-openjdk

# Build app
RUN mvn install

#make entrypoint shellscript executable
RUN chmod +x run_echoapp.sh

RUN cp target/echoapp-0.0.1-SNAPSHOT.jar .

RUN yum remove -y java

# Define commonly used JAVA_HOME variable
ENV PATH=/usr/lib/jvm/java-11-openjdk/bin/:$PATH
ENV JAVA_HOME /usr/lib/jvm/jre-11-openjdk
ENV CLASSPATH $JAVA_HOME/lib

EXPOSE 8181

# Define entrypoint to start with.
ENTRYPOINT ["/cmd/run_echoapp.sh"]

