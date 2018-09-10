#!/bin/bash

#### To backup the MySQL database daily, call this script from a cron job
echo "Started backup on `date '+%m-%d-%Y'`"
/bin/mysqldump --all-databases | gzip > /opt/answer/backups/answer_`date '+%m-%d-%Y'`.sql.gz
find /opt/answer/backups/ -type f -name '*.gz' -mtime +15 -exec rm {} \;
echo "End of backup"