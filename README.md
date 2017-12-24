# cryptoManager
The goal of this application is to manage your cryptocurrency in a simple manner. This application shows the current state of your portfolio and the value of your assets. It provides you tools to give insight in the values over time.

This application is purely for administrative purposes. You can't actually transfer, sell, buy any cryptocoins with it. 

## Installation
To install the cryptoManager application, follow the following steps:

First create a clone of this repository
```
git clone https://github.com/kolkos/cryptoManager.git
```

### Create keystore, change the passwords
Now you have to create a keystore (for https to work)
source: https://memorynotfound.com/spring-boot-configure-tomcat-ssl-https/
```
cd cryptoManager
cd src/main/resources
keytool -genkey \
-alias tomcat-localhost \
-keyalg RSA \
-keysize 2048 \
-validity 3650 \
-keystore keystore.jks
```
Answer the questions
Remember the password(s)

Now open the application.yml file
```
nano application.yml
```

Change the passwords
```
server:
  port: 8443
  ssl:
    enabled: true
    key-alias: tomcat-localhost
    key-password: changeme
    key-store: classpath:keystore.jks
    key-store-provider: SUN
    key-store-type: JKS
    key-store-password: changeme
```

### Run with docker
The app can run in docker containers. The stack contains a application and a database container. 

Run the stack with the following command
```
# first open the root folder of this project
docker-compose up -d
```

After updating the application, run the following command to rebuild the app
```
docker-compose build
```

This will rebuild the application

### First run
By default the database is empty. You can create two users (and their necessary roles) by opening a browser and go to:
```
https://your.host.name:8443/install
```

This will create a administrator and normal user. 

At this moment there is no administration panel available. So the administrator can do exactly the same as a normal user. 
