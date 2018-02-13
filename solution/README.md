# DropWizard Expenses Application

Introduction
---

The purpose of this application is to demonstrate the usage of the DropWizard API in conjunction with dockerized 
PostgreSQL DB and AngularJS frontend.

How to run
---

This application was developed and tested on OSX Sierra and CentOS 7.4.1708.

### Prerequisites
1. [Node package manager](https://docs.npmjs.com/getting-started/installing-node)
2. [Docker](https://docs.docker.com/install/)
3. [Maven 3.x](https://maven.apache.org/install.html)
4. [Java 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)

### Setting up the development database

1. Run the DB container by executing  `docker run -d -p 32768:5432 -P --name engage jacarlik/postgres_service:latest`
2. Check if the database is accessible by running `psql -h localhost -p 32768 -d expenses -U engage -c 'SELECT 1;'`. 
The password should be same as the username. If you can't access the DB, you might have docker-machine installed.
To the get correct IP address, required to access the DB, run `docker-machine ip default`.
 
### Backend

1. Go to the directory of your choice and execute `git clone https://github.com/jacarlik/dropwizard_demo.git && cd dropwizard_demo/solution`.
2. If you're using docker-machine (mentioned in the step 2),  you need to update the `uri` property with the correct IP address (instead of `localhost`) in `src/main/resources/profiles/mainline.yml`.
3. Run `mvn clean package -U` to build the application
4. Start the application with `java -jar target/expenses-1.0-SNAPSHOT.jar server src/main/resources/profiles/mainline.yml`
5. To check that the application is running enter URL `curl 'http://localhost:10001/healthcheck'`

The following cURL commands can be used to interact with the endpoint:

#### Health Check

    curl 'http://localhost:10001/healthcheck'
    {
      "Expenses Application": {
        "healthy": true,
        "message": "Expenses service is healthy"
      },
      "deadlocks": {
        "healthy": true
      }
    }
  
#### Get all expenses

    curl 'http://localhost:10000/app/expenses/' -H "Authorization: Basic YWRtaW46YWRtaW4="
    [
      {
        "date": "09/02/18",
        "amount": 10.2,
        "vat": 1.7,
        "reason": "Gasoline 50L"
      },
      {
        "date": "04/02/18",
        "amount": 20.2,
        "vat": 3.37,
        "reason": "Business lunch"
      },
      {
        "date": "01/02/18",
        "amount": 10120,
        "vat": 1686.67,
        "reason": "Louis Vuitton HARLEM ANKLE Boots"
      }
    ]
#### Get a single expense
    curl 'http://localhost:10000/app/expenses/1' -H "Authorization: Basic YWRtaW46YWRtaW4="
    {
      "date": "09/02/18",
      "amount": 10.2,
      "vat": 1.7,
      "reason": "Gasoline 50L"
    }
#### Save an expense

    curl -v -X POST 'http://localhost:10000/app/expenses' -d '{"date":"12/02/18", "reason":"Test #1", "amount": 1000}' -H "Content-Type: application/json" -H "Authorization: Basic YWRtaW46YWRtaW4="

#### Delete an expense
    curl -X "DELETE" 'http://localhost:10000/app/expenses/1' -H "Authorization: Basic YWRtaW46YWRtaW4="

### Frontend
1. Within the project root run `npm install -g gulp && npm install` and then `gulp`
2. Access the UI by hitting http://localhost:8080/#/expenses
