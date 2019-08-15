# OncoKB Genie Portal Container

### Steps to create the docker container:
1. Place project files in this directory

2. `docker build -t lookup_tool:latest .`

3. `docker run --network="host" -p 5252:5252 lookup_tool`


### Dockerfile Explanation (Line-by-Line)
- Use the official Python3.7-slim runtime environment as a parent image
- Copy the current directory's contents to `/app`
- Change the working directory to point to the newly created directory
- Install the required packages that are specified within `requirements.txt` using the UTSW proxy
- Specify the UTSW http proxy server
- Specify the UTSW https proxy server
- Do not apply the proxy server on localhost addresses
- Allow port 5252 to be accessed outside the container
- Run the command `python App.py` to launch the application

### Notes

- In order to connect to the container's localhost, `--network="host"` must be added to the docker run command. For more information, [see the Docker's host network documentation.](https://docs.docker.com/network/host/)
- `xlrd>=1.0.0` was added to the `requirements.txt` file as it is needed for the application to operate
