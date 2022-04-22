#!/bin/bash

cd ..

gradle build

mkdir -p build

build() {
    echo "#!/bin/java -jar" > build/$1
    cat $1/build/libs/$1.jar >> build/$1
    chmod +x build/$1
}

build forger
build node-api
build node-full
build node-miner
build webserver

cd scripts