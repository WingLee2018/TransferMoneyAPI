# Transfer Money API


## APIs:


- Get balance - Retrieve balance of an account

- Create transaction - Create a transaction to transfer money to another account


## API Specification

https://github.com/WingLee2018/TransferMoneyAPI/blob/master/API_Specification.pdf


## Start the application


mvn spring-boot:run


## Run tests


mvn test


## Commands and Examples

Note: For test purpose, only 3 account ids (user1, user2, user3) are valid.


#### Success:


1. curl http://localhost:8080/accounts/user2/balance


{"balance":5000000.0}


2. curl -H "content-type: application/json" -X POST -d '{"recipient_account_id":"user2","transfer_amount":30000}' http://localhost:8080/accounts/user1/transaction

{"id":"0ZHyNyiilo","new_balance":70000.0}


#### Error:


1. curl http://localhost:8080/accounts/abc/balance


{"error_code":404,"error_description":"Account abc is not found"}


2. curl -H "content-type: application/json" -X POST -d '{"recipient_account_id":"user3","transfer_amount":3000000}' http://localhost:8080/accounts/user1/transaction


{"error_code":400,"error_description":"Account has insufficient fund"}

## Third Party Library

- SpringFox - generate Json API document
