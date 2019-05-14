#!/bin/bash
filebeat -e -E LOGGING_FILE="$1"
