# Blockchain

## Installation

```bash
git clone https://github.com/LuMug/blockchain
cd blockchain
```

### Unix

There is a handy script to compile everything and generate
unix executables in the `build/` folder

```
chmod +x scripts/build.sh
scripts/build.sh
cd build
```

This will generate a folder containing

```
api
forger
miner
node
seeder
webserver
```

### Windows

Compile using gradle and directly run the JARs

```bash
./gradlew.bat build
java -jar <module>/build/libs/<module>.jar
```