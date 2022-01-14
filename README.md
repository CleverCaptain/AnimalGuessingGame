# AnimalGuessingGame
This is a game where the computer tries to guess the animal an user is thinking

## General information
- In this game the computer tries to guess the animal an user is thinking by asking yes/no questions


## Programming Language
- Java 16 was used to complile and run this code
- Jackson library was also used to save data in the form of xml or json

## Usage
- When using it for the first time the computer asks about the user's favorite animal
- From the second run onwards it guesses the animal by asking yes/no questions
- If the computer's guess is incorrect, then it asks about the difference between
guessed animal and correct animal.
- Based on the input it updates it's knowlege tree
- As the number of run's increases, the computer's knowlege base also increases

## Room for improvement
- This uses a binary search tree to make decisions
- Because of this only yes/no questions are supported
- Instead one can use a (non-binary) tree to allow the computer to ask more general questions
- This can improve the accuracy of the knowledge base, especially in the long run

