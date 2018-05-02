#!/bin/sh
BackupPath=/home/ccoe-user/safecampus-backup
CurrentMonth=$(date +%Y%m)
CurrentTime=$(date +%Y%m%d%H%M%S)

if [ ! -d "$BackupPath" ]; then
mkdir $BackupPath
fi

if [ ! -d "$BackupPath/$CurrentMonth" ]; then
mkdir $BackupPath/$CurrentMonth
fi

zip -q -r $BackupPath/$CurrentMonth/$CurrentTime /home/ccoe-user/safecampus