FROM python:3.7-slim

COPY . /app

WORKDIR /app

RUN pip install --proxy http://proxy.swmed.edu:3128 --trusted-host pypi.python.org -r requirements.txt

ENV http_proxy http://proxy.swmed.edu:3128
ENV https_proxy http://proxy.swmed.edu:3128
ENV no_proxy=localhost,127.0.0.0,127.0.1.1,127.0.1.1,local.home

EXPOSE 5252

CMD ["python", "App.py"]
