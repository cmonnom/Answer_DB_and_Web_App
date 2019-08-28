from App import app as application


if __name__ == "__main__":
  application.config["APPLICATION_ROOT"] = '/lookup'
  application.run()