#!/bin/bash
RESTFUL_CALL_API=$1
shift
curl -v http://${RESTFUL_CALL_HOST:-localhost}:${RESTFUL_CALL_PORT:-8080}/api/${RESTFUL_CALL_API} -H'Content-Type: application/json' $*
