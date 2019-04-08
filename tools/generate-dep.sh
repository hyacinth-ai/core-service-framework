#!/bin/bash
gradle dotDep
dot -Tpng project-dependencies.dot > project-dependencies.png
open ./project-dependencies.png
