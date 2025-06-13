Certainly! Here's a complete `README.md` written in English for your Spring Boot application using Firebase:

---

```markdown
# Spring Boot Firebase Application

This is a Spring Boot application that integrates with Firebase Admin SDK to manage resources like Boards and Cards.

---

## Features

- Firebase integration with Firestore
- RESTful APIs for boards and cards
- JWT-based authentication
- Modular Spring Boot architecture

---

## Prerequisites

Make sure you have the following installed:

- Java 21+
- Maven 3.8+
- A Firebase project with a service account

---

```
## Project Structure
<pre> src/ ├── main/ 
        ├── java/  
             └── com/ 
                └── huydev/ 
                    └── skipli_be/ 
                        ├── config/
                        ├── constant/ 
                        ├── controller/ 
                        ├── dto/ 
                            ├── request/ 
                            └── response/ 
                        ├── entity/ 
                        ├── exception/ 
                        ├── repository/ 
                        ├── service/ 
                        ├── utils/ 
                    └── SkipliBeApplication.java 
            └── resources/ 
</pre>
````

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/your-springboot-firebase-app.git
cd your-springboot-firebase-app
````

### 2. Add Firebase Service Account

* Go to your [Firebase Console](https://console.firebase.google.com/)
* Navigate to `Project Settings > Service accounts`
* Click "Generate new private key"
* Download the `firebase-admin.json` file
* Place it in the `src/main/resources/` directory of the project

```
src/main/resources/firebase-admin.json
```


## Install Dependencies

Use Maven to install all dependencies:

```bash
mvn clean install
```

---

## Run the Application

To start the application locally:

```bash
mvn spring-boot:run
```

---

