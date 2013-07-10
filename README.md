# Simple Java Web Service Template
* This is a Java web service template which uses distributed cache over persistent data store (MongoDB in this case).
* It's a framework, ideal for scaling read heavy web applications.
* Current implementation uses MongoDB for persistent storage and, can support Memcached or Couchbase for distributed caching and EhCache for local caching.
* Cache invalidation happens during create/update.
* The Java WS framework is mostly servlet web services hosted on Apache Tomcat container.

<b>P.S.</b> thanks to <a href='https://github.com/atsikiridis'>Artemis</a> and <a href='https://github.com/geotsiros'>George</a> for implementing support for Redis, MySQL and Cassandra

# Technologies and Frameworks used
* __Spring Framework (for DI)__
* __Apache CXF Framework__
* __Google Guava__
* __Apache Commons__
* __Spring Data (for MongoDB integration)__
* __Jackson JSON Processor__
* __Couchbase__
* __Memcached__
* __EhCache__
* __Apache Tomcat__
* __Maven__
* __Java__
* __Redis__
* __MySQL__
* __Cassandra__


# Prerequisites
* MongoDB

Download it from <a href='http://www.mongodb.org/downloads'>here</a>

start it using following command:

		mongod --fork --master --logpath /tmp/mongodb.log --logappend --noauth


* Memcached/Couchbase

you either need memcached or couchbase cache (for distributed caching)

- Memcached

Download it from <a href='http://memcached.org/'>here</a>

start is using following command:

		memcached -d
		
start it on a different port using following command:

		memcached -d -p 11212


- Couchbase

Download it from <a href='http://www.couchbase.com/download'>here</a>


# Build
		git clone https://github.com/parekhparth/SimpleJavaWS.git

		cd SimpleJavaWS

		mvn clean install
(above command runs the tests and builds web-service-1.0-SNAPSHOT.war)


# Setup

Configuration file can be found <a href='https://github.com/parekhparth/SimpleJavaWS/blob/master/service/src/main/resources/META-INF/configuration.properties'>here</a>

Change the "cache.type=memcached" to whatever cache you're using.

For e.g.: If you're using multiple Memcached, change the "memcached.cache.connection.uri=localhost:11211,localhost:11212" appropriately

Assuming maven and everything else is setup; you're ready to test the web service.


# Web Service

Web service reads from cache and writes to disk. Cache invalidation happens during update and new value is pushed to cache after successful DB update

The example web service implemented is "Product Web Service" which will let you can create, update and retrieve one or all the products.

Produt model is described below:


## Product Data Model
Product contains Name, Description, Price and Status (ACTIVE, DISABLED)


# Build
after setup, you can run the tests using following command:

		mvn test

# Execute
after all the tests are succesfully run; you can build the WAR and start the Tomcat server using:

		mvn clean install
		mvn tomcat:run

above command will start the server on 8110 port. You can run following example commands to test web service:

* Create Product

```
	curl -i -H "Content-Type: application/json" -X POST -d \
	'{
		"name": "Product name 1",
		"description": "This is a description of product name 1",
		"price": 0.99
	}' 'http://localhost:8110/ws/products/v1'
```

```
	{
		"id" : "51337401036431a3dfc546bf",
		"name" : "Product name 1",
		"description" : "This is a description of product name 1",
		"status" : "ACTIVE",
		"price" : 0.99
	}
```

* Get Product

```
	curl "http://localhost:8110/ws/products/v1/51337401036431a3dfc546bf"
```

```
	{
		"id" : "51337401036431a3dfc546bf",
		"name" : "Product name 1",
		"description" : "This is a description of product name 1",
		"status" : "ACTIVE",
		"price" : 0.99
}
```

* Update Product

- update all fields

```
	curl -i -H "Content-Type: application/json" -X PUT -d \
	'{
		"name": "Product name 0",
		"description": "This is a description of product name 0",
		"price": 0.99,
		"status": "ACTIVE"
	}' 'http://localhost:8110/ws/products/v1'
```
```
	{
		"id" : "51337401036431a3dfc546bf",
		"name" : "Product name 0",
		"description" : "This is a description of product name 0",
		"status" : "ACTIVE",
		"price" : 0.99
	}
```
- update only description and status

```
	curl -i -H "Content-Type: application/json" -X PUT -d \
	'{
		"description": "This is a description of product name 0",
		"status": "DISABLED"
	}' 'http://localhost:8110/ws/products/v1'
```
```
	{
		"id" : "51337401036431a3dfc546bf",
		"name" : "Product name 0",
		"description" : "This is a description of product name 0",
		"status" : "DISABLED",
		"price" : 0.99
	}
```

* Get all Products

```
	curl "http://localhost:8110/ws/products/v1"
```

```
	[ {
		"id" : "51337401036431a3dfc546bf",
		"name" : "Product name 0",
		"description" : "This is a description of product name 0",
		"status" : "DISABLED",
		"price" : 0.99
	}, {
		"id" : "513375b2036431a3dfc546c0",
		"name" : "Product name 2",
		"description" : "This is a description of product name 2",
		"status" : "ACTIVE",
		"price" : 10.99
	}, {
		"id" : "513375be036431a3dfc546c1",
		"name" : "Product name 1",
		"description" : "This is a description of product name 1",
		"status" : "ACTIVE",
		"price" : 5.99
} ]
```

# TODO
* document Redis, MySQL and Cassandra setup
* add load test numbers


# LICENSE

This project is under "Do whatever you want" MIT License => http://www.tldrlegal.com/license/mit-license

However, all the other Open source frameworks/technologies used in this project come with their own respective licenses.
