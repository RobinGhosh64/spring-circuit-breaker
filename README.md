# Circuit Breaker Usage - One service calling another service

<!-- TOC -->
**Overview**: 

- [Requirements](#requirements)
- [What is a circuit breaker pattern](#what-is-a-circuit-breaker-pattern)
- [Step 2: Create an Azure Resource Group ](#step-2-create-an-azure-resource-group)
- [How the pattern works?](#how-the-pattern-works?)
- [Life Cycle of Pattern States](#life-cycle-of-pattern-states)
- [Pattern states](#pattern-states)
- [What is Resilience4j](#what-is-resiliency4j)
- [Create 2 Micro Services ](#create-2-micro-services)
- [Rate Service](#rate-service)
- [Loan Service](#loan-service)


- [Loan Service calls Rate Service](#loan-service-calls-rate-service)
- [Normal conditions](#normal-conditions)
- [Break conditions](#break-conditions)
- 
<!-- TOC -->

## Objectives 

Azure Serverless offerings provide a wide array of capabilites to drive modern application architectures with zero-infrastructure and capital investments. This lab demonstrates Azure PaaS offerings including: 

- Azure Event Grid
- Azure Function Apps 
- Azure Cosmos DB 
- Azure infrastructure services to include resource groups, storage accounts and familiarization with Azure CLI services

**NOTE: This exercise is a section of a day-long presentation on Azure App Innovation, the complete workshop may be found [here](https://aka.ms/azuredevdaylabs).**

## Requirements

This example assumes the user already has an Azure subscription with contributor access. Additionally, the following services will be required during the lab: 

- Azure CLI, [How to install Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli)
- Git Bash, [Git Download for Windows](https://gitforwindows.org/)

## Step 1: Setup Azure subscription and make sure you can  sign in 

Initial login and subscription setup is a required prerequisite


NOTE: The region location of the Resource Group may be different than the Azure resources 

## Step 2: Create an Azure Resource Group 

  Navigate to the **Resource Groups** from the top main search bar. Select **Create** to create a New Resource Group
  
  Fill all paramters as shown:
  
- Subscription: **your-subscription-name**
- Resource Name: **devdayfeb-rsg** 
- Region: **East US** 


Click on **Review + create** and then confirm final creation

 <img src="media/devday-create-resource.png">
 
  



Micro services are the most important implementation aspect used in the industry now. With the use of micro service architecture, developers could eliminate so many issues they had previously with monolithic applications. Moving forward, people started to search and adopt various patterns into the micro services. Most of the times, new pattern was originated to address a common issue seen in another pattern. In this way, a lot of patterns came into the practice with evolution of time. You can get a whole summary from here: https://microservices.io/patterns/microservices.html

These micro service patterns were further broken down into several categories, considering their scopes. Among all these patterns, there are some very important and popular patterns used by so many developers. Circuit Breaker is one of them which helps to manage downstream service failures in a proper manner. Let’s understand what this pattern does. 💪

## What is Circuit Breaker Pattern?
You may have already heard of circuit breakers we find in electronic items. What is the main purpose of it? Simply, break the electric flow in an unexpected scenario. Same as that, here also this micro service pattern has go the name due to the same nature it has.

This pattern comes into the picture while communicating between services. Let’s take a simple scenario. Let’s say we have two services: Service A and B. Service A is calling Service B(API call) to get some information needed. When Service A is calling to Service B, if Service B is down due to some infrastructure outage, what will happen? Service A is not getting a result and it will be hang by throwing an exception. Then another request comes and it also faces the same situation. Like this request threads will be blocked/hanged until Service B is coming up! As a result, the network resources will be exhausted with low performance and bad user experience. Cascading failures also can happen due to this.

In such scenarios, we can use this Circuit Breaker pattern to solve the problem. It is giving us a way to handle the situation without bothering the end user or application resources.

## How the pattern works? 💥
Basically, it will behave same as an electrical circuit breaker. When the application gets remote service call failures more than a given threshold circuit breaker trips for a particular time period. After this timeout expires, the circuit breaker allows a limited number of requests to go through it. If those requests are getting succeeded, then circuit breaker will be closed and normal operations are resumed. Otherwise, it they are failing, timeout period starts again and do the rest as previous.

Let’s figure out this using the upcoming example scenario that I’m going to explain. 😎

## Life Cycle of Pattern States 💥
There are 3 main states discussed in Circuit Breaker pattern. They are:

CLOSED
OPEN
HALF OPEN
Let’s understand the states briefly….

CLOSED State
When both services which are interacting are up and running, circuit breaker is CLOSED. Circuit breaker is counting the number of remote API calls continuously.

OPEN State
As soon as the percentage of failing remote API calls is exceeding the given threshold, circuit breaker changes its state to OPEN state. Calling micro service will fail immediately, and an exception will be returned. That means, the flow is interrupted.

HALF OPEN State
After staying at OPEN state for a given timeout period, breaker automatically turns its state into HALF OPEN state. In this state, only a LIMITED number of remote API calls are allowed to pass through. If the failing calls count is greater than this limited number, breaker turns again into OPEN state. Otherwise it is CLOSED.


## Pattern states
To demonstrate the pattern practically, I will use Spring Boot framework to create the micro services. Resilience4j library is used to implement the circuit breaker.

## What is Resilience4j?
Resilience4j is a lightweight, easy-to-use fault tolerance library inspired by
Netflix Hystrix. It provides various features.

Circuit Breaker — fault tolerance
Rate Limiter — block too many requests
Time Limiter — limit time while calling remote operations
Retry Mechanism — automatic retry for failed operations
Bulkhead — limit number of concurrent requests
Cache — store results of costly remote operations
We are going to use the very first feature with Spring Boot in this article. 😎

Let’s start!!

## Create 2️⃣ Micro Services
I’m going to implement a simple inter service communication scenario using two services called loan-service and rate-service.

Technical details:
Spring Boot with H2 in-memory DB, JPA, Hibernate, Actuator, Resilience4j

Scenario:
Loan service can fetch Loans saved in DB and each loan object has loan type. There are separate interest rate percentages according to the loan type. So, Rate service is having those Rate object details with it’s name.

I will make a call to Rate service from Loan service requesting the interest rate of the given loan type.
Then I have to calculate the total interest value for the loans depending on their loan type.
Then I will update all the Loan objects with the interest amount using the rate I got from Rate service.

Project setup
Since rate-service is independent, I will first implement basic functionalities for the rate-service.

Create a new Spring Boot project with the dependencies provided inside below POM file. I have named it as rate-service.

## Rate Service

## Loan Service

https://github.com/iRobinGhosh64/spring-boot-circuit-breaker/



