cd /home/ccoe-user/safecampus
jar -xvf safecampus-1.1-SNAPSHOT.war
rm -rf safecampus-1.1-SNAPSHOT.war
cd /home/ccoe-user/app/tomcat/apache-tomcat-8.5.24/bin
./daemon.sh start
