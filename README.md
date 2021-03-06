# Transaction_Across_Microservice
REST Transaction Across Microservice

2 Phase commit

1. Begin Transaction
2. Make a REST call to other microservice
3. Call wait() on that thread.
4. Now, if all calls are succesfull, commit transaction by notifying that thread.
5. Else, rollback that thread by using Future.cancel(true)
