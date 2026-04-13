
<div align="center">

  <h1>BOOKMYSEAT</h1>

  <p>"The Movie Library" is a comprehensive information system developed for a network of movie theaters to enhance business processes and improve customer experience.
  
  The system provides features like browsing movie listings, purchasing tickets, accessing home viewing options, managing multi-entry tickets, and optimizing customer service.</p>


  <!-- Badges -->
  <p>
    <img src="https://img.shields.io/github/languages/top/VirajSejpal/BookMySeat-main?color=red" alt="language" />
    <img src="https://img.shields.io/github/languages/code-size/VirajSejpal/BookMySeat-main?color=informational" alt="code size" />
  </p>

   <!-- Links -->
   <h4>
      <a href="#setup--installation">Installation</a>
      <span> · </span>
      <a href="#key-features">Key Features</a>
      <span> · </span>
      <a href="#system-components">System Components</a>
      <span> · </span>
      <a href="#system-architecture">System Architecture</a>
   </h4>


</div>



## Setup & Installation

> [!IMPORTANT]
> To set up the project locally, make sure you have **Java**, **Maven**, and **MySQL** installed before proceeding.


**Clone the Repository**:
   ```bash
   git clone https://github.com/VirajSejpal/BookMySeat-main.git
   ```

> [!NOTE]
> The project consists of three main modules that must be built together.

1. **client** - a simple client built using JavaFX and OCSF. We use EventBus (which implements the mediator pattern) in order to pass events between classes (in this case: between SimpleClient and PrimaryController).
2. **server** - a simple server built using OCSF.
3. **entities** - a shared module where all the entities of the project live.

## Running

> [!CAUTION]
> Ensure your `MySQL` service is running and the database name matches exactly `bookmyseat`.

1. Run Maven install **in the parent project**.
2. Run the server using the exec:java goal in the server module. ! it will ask you for your database password.
3. Run the client using the javafx:run goal in the client module.
4. Enjoy!

## Key Features

> [!NOTE]
> Each feature below corresponds to a major subsystem of the platform and was developed with modularity and scalability in mind.

1. **Movie Listings**: Allows users to browse a list of current and upcoming movies, with detailed information such as titles (in Hebrew and English), main actors, directors, producers, and synopses. Additionally, users can view movie posters and trailers.

2. **Ticket Purchase**: Facilitates the purchase of tickets for movie screenings at theaters. Users can select seats through an interactive map and complete payments securely. After the purchase, users receive an email confirmation with the details of the transaction.

3. **Home Viewing Links**: Enables the purchase of links for home viewing of selected movies, available for a limited time. Users receive email reminders one hour before the link activation.

4. **Multi-Entry Tickets**: Offers discounted multi-entry tickets that provide access to multiple screenings at any theater in the network, subject to seat availability.

5. **Customer Service**: Provides a robust framework for handling customer complaints and managing refunds efficiently. Customer service representatives can process refunds, offer compensations, and ensure timely responses to complaints.

6. **Operational Monitoring**: Generates monthly reports for operational monitoring, including ticket sales, complaint handling, and overall performance metrics.

## System Components

> [!NOTE]
> The architecture implements a **Role-Based Access Control (RBAC)** structure, organizing responsibilities across clearly defined layers.  
> Each module enforces permissions and data access according to user roles - ensuring scalability, maintainability, and secure operation.

- **Theater Network Management**: Manages multiple theaters across different locations, each with unique configurations, including halls, seating arrangements, and operational schedules.

- **Movie and Screening Management**: Keeps an updated list of movies and their scheduled screenings, with the ability to manage future releases and promotions.

- **Ticketing and Sales Management**: Handles all aspects of ticket sales, both for in-theater screenings and home viewing, with options for refunds and exchanges based on specific rules.

- **Customer and Employee Management**: Supports user authentication and authorization, allowing different access levels for customers, employees, and managers.

- **Data Analysis and Reporting**: Produces detailed reports that help in analyzing sales trends, customer behavior, and operational efficiency, aiding in strategic decision-making.

## System Architecture

- **Distributed Client-Server Model**: The system is built on a client-server architecture, supporting multiple users simultaneously from different endpoints.
- **Relational Database**: Utilizes a relational database to store and manage data related to movies, screenings, users, and transactions.
- **Java-Based Development**: The project is developed in Java, providing a stable and scalable platform that can be extended for internet-based access in future versions.


---

Thank you for using **BOOKMYSEAT**!  
We hope it enhances your movie experience and demonstrates best practices in modular system design.
