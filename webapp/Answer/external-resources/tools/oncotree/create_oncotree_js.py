# -*- coding: utf-8 -*-
"""
Created on Fri Sep 13 10:38:04 2019
To create a drop down of oncotree codes in open_case.js
This script will take the json of http://oncotree.mskcc.org/api/tumorTypes
and create a javascript file that should be placed in resources/js/oncotree.js

The variable can also be used to determine the tissue type from an oncotree cod
e
@author: Guillaume
"""
import json
import io
import requests
import pandas as pd

url="http://oncotree.mskcc.org/api/tumorTypes"
proxy={"http": "http://proxy.swmed.edu:3128", "https": "http://proxy.swmed.edu:3128"}

s= requests.get(url, proxies=proxy).content
df = pd.read_json(io.StringIO(s.decode('utf-8')))


df = df[["code", "name", "tissue"]]
df.columns = ["text", "label", "tissue"]
df.sort_values("text", inplace=True)

json_records = json.loads(df.to_json(orient="records"))
javascript = "var oncotree = "
javascript = javascript + json.dumps(json_records, indent=2)

output_file = open("oncotree.js", "w")
output_file.write(javascript)
output_file.close()
