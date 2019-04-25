#!/bin/bash
BOOTJAR=$(find ./build -name '*boot*.jar') 
java -jar "$BOOTJAR" @*
