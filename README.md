# GitHub Info Application

A Spring Boot application that integrates with the GitHub API to fetch user repository information and branches, while handling user errors and server errors.


## Description

This application allows you to retrieve GitHub repositories for a specified user, filtering out forks, and then fetching branches for each repository. It also handles global exceptions and server errors.

## Features

- Fetch user repositories from GitHub.
- Retrieve branches for repositories.
- Error handling:
    - `UserNotFoundException` for non-existent users.
    - General server errors.

## Requirements

- JDK 11 or higher
- Maven

## Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/kuba1306/githubinfo.git

2. **Navigate to the project directory**
3. **Switch to the first-branch branch**
4. **Run the application:**
5. **To obtain a response, use a tool like Postman to send a GET request to:**

   ```bash
   http://localhost:8080/{username}

6. **Replace {username} with the actual GitHub username you want to query**
7. **Ensure the Accept header is set to application/json.**
