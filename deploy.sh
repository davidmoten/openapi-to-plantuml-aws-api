#!/bin/bash
set -e
PARAMS="$1 $2 $3 $4 $5 $6 $7 $8"
mvn clean package aws:property@prop aws:deployFileS3@file aws:deployCf@cf aws:deployRestApi@api -Dmode=$MODE $PARAMS 
