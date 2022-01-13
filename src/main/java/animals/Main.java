package animals;

import java.util.Scanner;

import static java.lang.System.in;

public class Main {

    //Internationalization and Localization not implemented.

    public static void main(String[] args) {
        String type = "json";
        Scanner kb = new Scanner(in);
        if (args.length != 0) {
            type = args[0];
        }
        AnimalAssistant assistant = new AnimalAssistant("animals." + type);
        assistant.loadKnowledgeBase(kb);

//        System.out.println(app.getString("welcome"));
        System.out.println("Welcome to the animal expert system!");
        System.out.println();
        boolean isPlaying = true;
        while (isPlaying) {
            System.out.println("What do you want to do:");
            System.out.println();
            System.out.println("1. Play the guessing game\n" +
                    "2. List of all animals\n" +
                    "3. Search for an animal\n" +
                    "4. Calculate statistics\n" +
                    "5. Print the Knowledge Tree\n" +
                    "0. Exit");
            int choice = kb.nextInt();
            switch (choice) {
                case 1 -> assistant.playGame(kb);
                case 2 -> assistant.printAllAnimals();
                case 3 -> {
                    System.out.println("Enter the animal:");
                    assistant.printAnimalFacts(kb.next());
                }
                case 4 -> assistant.printStatistics();
                case 5 -> assistant.printTree();
                case 0 -> isPlaying = false;
                default -> System.out.println("Input a number from 0 to 5 to choose what to do!");
            }
        }
    }
}
