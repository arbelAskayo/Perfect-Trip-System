# Perfect Trip System

## Table of Contents
1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Setup Instructions](#setup-instructions)
4. [Usage](#usage)
5. [Code Overview](#code-overview)


## Introduction

The Perfect Trip System is a comprehensive Java-based application designed to help users plan and manage trips efficiently. It offers functionalities to handle places, trips, reviews, and members, ensuring a seamless experience for travelers and trip planners.

## Project Structure

Perfect-Trip-System/
├── src/
│ ├── boundry/
│ ├── control/
│ ├── entity/
│ ├── exceptions/
│ └── utils/
└── README.md

- **boundry**: Contains the user interface elements and panels.
- **control**: Contains the control logic for the application, including classes for managing content, culture, members, reviews, searches, and trips.
- **entity**: Contains the entity classes representing the main data models like `City`, `Country`, `Hotel`, `Member`, `Place`, `Restaurant`, and more.
- **exceptions**: Contains custom exception classes used throughout the application.
- **utils**: Contains utility classes used for various purposes such as accommodation styles and price levels.

## Setup Instructions

1. **Clone the repository:**
    ```bash
    git clone https://github.com/arbelAskayo/Perfect-Trip-System.git
    cd Perfect-Trip-System
    ```

2. **Open the project in your preferred IDE (e.g., IntelliJ IDEA, Eclipse).**

3. **Configure the Database:**
   - Ensure you have the necessary database setup as defined in `Consts.java`.
   - Update the `DB_FILEPATH` in `Consts.java` to point to your local database file.

4. **Build the Project:**
   - Use your IDE to build the project, ensuring all dependencies are correctly set up.

5. **Run the Application:**
   - Execute the main class to start the application.

## Usage

1. **Importing Places from XML:**
   - Use the `ContentControl.importPlacesFromXML(String xmlFilePath)` method to import places from an XML file.

2. **Managing Members:**
   - Create, update, and delete members using the `MemberControl` class.

3. **Handling Trips:**
   - Create and manage trips using the `TripControl` class. Add places to trips, add travelers, change trip dates, and confirm trip configurations.

4. **Reviews:**
   - Add, update, delete, and fetch reviews for places using the `ReviewControl` class.

5. **Searching for Places:**
   - Use the `SearchControl.searchByCriteria` method to search for places based on various criteria.

## Code Overview

### boundry

Contains the user interface components and panels.

### control

Handles the main logic of the application. Key classes include:

- `ContentControl`: Manages content, including importing places from XML and generating distances.
- `CultureControl`: Manages cultural aspects like kitchen styles.
- `MemberControl`: Handles member-related operations.
- `ReviewControl`: Manages reviews for places.
- `SearchControl`: Provides search functionalities.
- `TripControl`: Manages trip-related operations.

### entity

Defines the main data models:

- `City`, `Country`, `Hotel`, `Member`, `Place`, `Restaurant`, `Review`, `Trip`.

### exceptions

Defines custom exceptions used throughout the application.

### utils

Contains utility classes:

- `AccomodationStyles`, `PriceLevel`, and other helper classes.
