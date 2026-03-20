# CEMENT

CEMENT is a student schedule planner project with a Java/Gradle backend and a separate frontend. Once setup has been completed as follows the project can be run by running:

```bash
cd backend
./gradlew build
./gradlew run
```

and accessing the application at http://localhost:7000/

## Requirements
Requires java to be installed and gradle

## Project Structure

```text
CEMENT/
├── backend/    # Java backend (Gradle project)
├── frontend/   # Frontend application
└── .gitignore
```

## Backend Setup

The backend is the actual Gradle project. After cloning the repository, make sure to import the Gradle project from the `backend` folder.

### Open in IntelliJ

1. Clone the repository.
2. Open IntelliJ IDEA.
3. Open the project folder.
4. In IntelliJ, locate `backend/build.gradle`.
5. Right-click `build.gradle` and choose **Import Gradle Project** or **Link Gradle Project**.
6. If prompted, set the **Project SDK/JDK** to an installed Java version.

### Why this is necessary

The Gradle files are inside the `backend` folder, not at the repository root. Because of that, IntelliJ may initially treat the files as a normal folder instead of a Gradle-managed Java project until `backend/build.gradle` is imported.

## Requirements

- Java JDK installed
- IntelliJ IDEA recommended
- Gradle wrapper included in `backend`

## Building the Backend

From the repository root:

```bash
cd backend
./gradlew build
```

## Troubleshooting

### Java files are not recognized correctly in IntelliJ

Make sure `backend/build.gradle` has been imported as a Gradle project.

### No runnable `Main` class appears

Make sure:

- the Gradle project is imported
- a valid JDK is set
- the class contains:

```java
public static void main(String[] args)
```

### `./gradlew` or `.\gradlew.bat` does not work

Make sure you are inside the `backend` directory, since that is where the Gradle wrapper files are located.

## Notes

- `backend` is the Java/Gradle project
- `frontend` is the separate client-side application
- Do not run Gradle commands from the repository root unless the project is restructured into a root-level Gradle build

## Unfinished Sprint Tasks
No MVP tasks are unfinished but some of the additional features we hoped to complete were not finished including:
- Color options for the calendar
- Saving and loading multiple different schedules
- Hover popups for course information anywhere

## Authors

Cole Amacker, Timothy Heiser, Max Ware, Ella Kocher
