#!/bin/bash

WORKING_DIR=`pwd`
SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

cd SCRIPT_DIR
cd ..

./gradlew build

mkdir -p build

build() {
    dest=''

    if [ -z "$2" ]; then
        dest=$1
    else
        dest=$2
    fi

    echo "#!/bin/java -jar" > build/$dest
    cat $1/build/libs/$1.jar >> build/$dest
    chmod +x build/$dest
}

build forger
build node-api api
build node-full node
build node-miner miner
build webserver
build seeder

cd $WORKING_DIR