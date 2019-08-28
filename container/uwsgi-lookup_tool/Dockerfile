FROM tiangolo/uwsgi-nginx-flask:python3.7

COPY ./conf /opt/conf/

RUN pip install --proxy http://proxy.swmed.edu:3128 --trusted-host pypi.python.org -r /opt/conf/requirements.txt

RUN useradd -u 58646 tomcat
RUN useradd -u 58647 answerbe
RUN usermod -aG tomcat answerbe
RUN usermod -aG answerbe tomcat

COPY ./app /app
