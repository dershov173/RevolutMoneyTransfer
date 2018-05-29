# RevolutMoneyTransfer test project
A RESTful API (including data model and the backing implementation) for money transfers between internal users/accounts.
# Technologies:
- Java 8;
- Restlet;
- h2 embedded data storage;
- JUnit 4;
- Maven;

# How to install:
cd RevolutMoneyTransfer

mvn clean install

cd target

java -jar revolut-money-transfer-jar-with-dependencies.jar

Server up and running on localhost:8080

# Add users and accounts
Let us now add two users:

POST http://127.0.0.1:8080/money_transfers/users {name: Pavel Zdorik}

POST http://127.0.0.1:8080/money_transfers/users {name: Mihail Levashov}

Check that two users were successfully added:

GET http://127.0.0.1:8080/money_transfers/users

Now we can assosiate the account with our users. Amount is initial balanace of money available for this account

POST http://127.0.0.1:8080/money_transfers/users/1/accounts {amount: 100}

POST http://127.0.0.1:8080/money_transfers/users/2/accounts {Amount: 150}

We can also update the balance for certain account by sending such http request

PUT http://127.0.0.1:8080/money_transfers/users/2/accounts/2 {amount:150} 

We will obtain something like :
{
    "accountId": 2,
    "amount": "300",
    "userId": 2,
    "version": 2
}
# Perform money transfer
Now we cann execute money transfer between accounts with id 1 and 2: 

POST http://127.0.0.1:8080/money_transfers/transactions
{
  amount: 150,
  
  from_account_id: 1,
  
  to_account_id: 2
  
}

In the considered model the transaction will be writed immedeatly to data storage only if it was completed success. Any other types of transactions state like idle or declined are not allowed. After completing the transaction we can prove that account balances were updated:

GET http://127.0.0.1:8080/money_transfers/users/1/accounts

GET http://127.0.0.1:8080/money_transfers/users/2/accounts
