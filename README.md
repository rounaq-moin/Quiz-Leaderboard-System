# Quiz Leaderboard System

Spring Boot solution for the Bajaj Finserv Health SRM Java qualifier.

## What it does

- Polls the validator API exactly 10 times using `poll=0` through `poll=9`
- Maintains a mandatory 5-second delay between poll requests
- Deduplicates events using `roundId + participant`
- Aggregates score per participant
- Sorts the leaderboard by `totalScore` descending
- Computes the total score across all participants
- Submits the leaderboard once to the validator

## Tech stack

- Java 17
- Spring Boot 3.2.5
- Gson
- Lombok

## Configuration

Update [application.properties](/D:/Bajaj%20task/src/main/resources/application.properties) before running:

```properties
quiz.regNo=YOUR_REG_NO
quiz.baseUrl=https://devapigw.vidalhealthtpa.com/srm-quiz-task
quiz.pollCount=10
quiz.delayMillis=5000
```

Replace `YOUR_REG_NO` with your actual registration number.

## Run locally

```bash
mvn spring-boot:run
```

Then trigger the process:

```bash
GET http://localhost:8080/quiz/run
```

## Expected behavior

The app:

1. Calls `GET /quiz/messages?regNo=...&poll=0..9`
2. Ignores duplicate event entries based on `roundId|participant`
3. Builds the final leaderboard
4. Sends `POST /quiz/submit`
5. Returns the computed leaderboard, total score, and success message

## Project structure

```text
src/main/java/com/example/quizfinal
├── client
├── config
├── controller
├── exception
├── model
└── service
```
