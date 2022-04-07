# Blockchain

## Start seeder
```bash
gradle :seeder:build
java -jar <port> seeder/build/libs/seeder.jar
```

## Start node
```bash
gradle :node:build
java -jar node/build/libs/node.jar
```

## Create wallet and transactions
Generate wallet
```bash
java -jar forger.jar -gen -out ./key.priv
```

Create transaction
```bash
java -jar forger.jar -priv ./key.priv -amount 10000 -out transaction.tx -to <address>
```

Create transaction (no HTTP request for lastHash)
```bash
java -jar forger.jar -priv ./key.priv -last ./last.tx -amount 10000 -out transaction.tx -to <address>
java -jar forger.jar -priv ./key.priv -first -amount 10000 -out transaction.tx -to <address>
```

Dump transaction file content
```bash
java -jar forger.jar -dump ./transaction.tx
```