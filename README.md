# Authify

**Authify** is an innovative application designed to enhance the authentication process through QR code technology. It provides a user-friendly interface for secure login and sign-up functionalities, leveraging MySQL for database management. Upon registration, users can generate personalized QR codes that can be easily scanned for authentication purposes.

## Features

- **User Authentication**: Secure login and registration using MySQL database.
- **QR Code Generation**: Automatically generate a QR code upon user registration, containing essential information.
- **QR Code Scanning**: Scan QR codes to retrieve and display user information, such as first name, last name, academic year, and specialty.
- **Intuitive Interface**: A simple and clean user interface that allows for easy navigation and interaction.

## How It Works

1. **Login and Sign-Up**:
   - Users can log in or sign up through the application’s login page.
   - Sign-up involves entering personal details, which are stored in the MySQL database.
  
     ![git1](https://github.com/user-attachments/assets/67951f47-8893-4f37-beda-e59a1a44de8a)
     
2. **QR Code Generation**:
   - Upon successful registration, a QR code is generated that encapsulates the user’s information.
   - The QR code is saved as an image file and displayed within the application.

3. **QR Code Scanning**:
   - Users can select an existing QR code image from their file system.
   - The application reads and decodes the QR code, extracting and displaying the stored information.
  
   ![git2](https://github.com/user-attachments/assets/4896c3c6-a9db-45ce-8df0-2ebd51a02de8)

## Technologies Used

- **JavaFX**: For building the user interface.
- **ZXing**: For QR code generation and decoding.
- **MySQL**: For user authentication and data storage.

## Getting Started

To get started with Authify, simply run the application and follow the on-screen instructions to log in or create a new account. The application will guide you through the QR code generation and scanning processes.


---

**Contact Information**
- GitHub: https://github.com/djeghlaflydia
