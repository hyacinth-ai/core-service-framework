#!/bin/bash
gradle dotDep
dot -Tsvg project-dependencies.dot > project-dependencies.svg
open ./project-dependencies.svg
