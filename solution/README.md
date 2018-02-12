# Expenses

How to start the Expenses application
---

1. Run `mvn clean package` to build the application
1. Start application with `java -jar target/expenses-1.0-SNAPSHOT.jar server src/main/resources/profiles/mainline.yml`
1. To check that your application is running enter URL `http://localhost:10000/app/expenses`

Health Check
---

To see is the application is healthy, enter URL `http://localhost:10001/healthcheck`
