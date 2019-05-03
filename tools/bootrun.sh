#!/bin/bash
BOOTJAR=$(find ./build -name '*boot*.jar') 
echo java -jar "$BOOTJAR" $*
java -jar "$BOOTJAR" $*
