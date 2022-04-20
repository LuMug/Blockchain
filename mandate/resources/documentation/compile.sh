#!/bin/bash

MAIN_FILE="documentation"

if [ "$1" = "--help" ] || [ "$1" == "-h" ]; then
    echo "usage: ${0} [--bibtex]"
    echo ""
    echo "--bibtex     Compile new bibtex references using biber"

    exit 0
fi

WORKING_DIR=`pwd`
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

cd $SCRIPT_DIR

lualatex "${MAIN_FILE}.tex"

if [ "$1" = "--bibtex" ]; then
    biber $MAIN_FILE
    lualatex "${MAIN_FILE}.tex"
    lualatex "${MAIN_FILE}.tex"
fi

mv "${MAIN_FILE}.pdf" ../../

cd $WORKING_DIR