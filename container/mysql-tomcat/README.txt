This directory has 2 containers:
- db: mysql server with scripts to create the Answer database (answer_db) in the db subfolder
- web: tomcat container which should mount 
	./web/answer (containing the directory structure mounted to /opt/answer, including /opt/answer/conf/answer.properties) 
	./web/webapps (containing Answer.war)