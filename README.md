# mvs-RPC-Java-Connector


## Introduction


This project (Metaverse RPC java connector) is a java library that is intended to provide a wrapper around JSON-RPC provided by Metaverse [MVS](https://www.mvs.org/).

It is the extension of Litecoin-Bitcoin-RPC-Java-Connector using [GitHub Pages](https://github.com/nitinsurana/Litecoin-Bitcoin-RPC-Java-Connector).

[MVS](https://www.mvs.org/)  JSON-RPC calls for [Metaverse API Call List](https://github.com/mvs-org/metaverse/wiki/Metaverse-API-Call-List).

This project assumes that the user who has a preliminary knowledge about Metaverse and know how to start RPC server.


## How to use

######  Test Envrionment Setup
* Start Metaverse Server (daemon).
* Go to [test folder](https://github.com/mvshub/mvs-RPC-Java-Connector/tree/master/src/test/java/org/mvs/rpcconnector),
copy config.yml.template to config.yml, and config the username and password. Strongly recommend to use https in production.
```
metaverse:
  host: 127.0.0.1
  username: xxxxxx
  password: xxxxxx
  port: 8820
  protocol: http
``` 
* Sample usage which can be refer to [MetaverseCreateAccountTest.java](https://github.com/mvshub/mvs-RPC-Java-Connector/blob/master/src/test/java/org/mvs/rpcconnector/MetaverseCreateAccountTest.java).
There are two call methods callAPIMethod and callAPIMethodAsynchronous in [CryptoCurrencyRPC.java](https://github.com/mvshub/mvs-RPC-Java-Connector/blob/master/src/main/java/org/mvs/rpcconnector/CryptoCurrencyRPC.java),
which can be acted as a synchronous/asynchronous rpc client call to Metaverse RPC server.

###### How to build with gradle
* gradle clean build or gradle clean build -x test (without test)
* gradle shadowJar (build shadow jar package)

# Todo

Add more [call methods/LIST](https://github.com/mvs-org/metaverse/wiki/Metaverse-API-Call-List#-blocks)