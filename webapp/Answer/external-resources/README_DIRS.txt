This document details the purpose of each directory listed inside the properties file described in WEB-INF/servlets.xml.
For reference only. 
You should copy this document under the root directory of Answer 
and make edits to reflect your configuration, additional directories etc.

We will assume all directories are under the following root path for clarity: /opt/answer/
/opt/answer/conf: location of the properties file. All relevant paths should be described in this file. See external-resources/application_template.properties
/opt/answer/files: for storing bams and pdfs. This directory should not be exposed to the Internet.
/opt/answer/links: links that are exposed to the Internet are created here and point to the /opt/answer/files/* directories
/opt/answer/logs: output of Answer access logs
/opt/answer/docs: location of all the documentation files (web files) generated with Sphinx
/opt/answer/utils/pdf: location of fonts and images used in the final PDF report