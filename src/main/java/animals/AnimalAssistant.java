package animals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.*;

public class AnimalAssistant {
    //Different ways to output a affirmative response.
    public final static Set<String> YES = Set.of("y", "yes", "yeah", "yep", "sure", "right",
            "affirmative", "correct", "indeed", "you bet", "exactly", "you said it");

    //Different ways to output a negative response.
    public final static Set<String> NO = Set.of("n", "no", "no way", "nah", "nope", "negative",
            "i don't think so", "yeah no");

    //Recognized Vowels
    public final static Set<String> vowels = Set.of("a", "e", "i", "o", "u");

    //Different ways to output a clarification response
    public final static List<String> CLARIFICATIONS = List.of(
            "I'm not sure I caught you: was it yes or no?",
            "Funny, I still don't understand, is it yes or no?",
            "Oh, it's too complicated for me: just tell me yes or no.",
            "Could you please simply say yes or no?",
            "Oh, no, don't try to confuse me: say yes or no.");

    //Different ways to output a farewell.
    public final static List<String> FAREWELLS = List.of("Bye", "GoodBye", "See you later",
            "See you next time", "Talk to you later", "See ya!", "Later!", "Have a good day.",
            "Have a good one", "Take care.", "Peace!", "Peace out!", "Bye bye!", "Farewell!");

    //Knowledge base File name
    private String fileName;

    //root node of this Assistant.
    private BinaryTreeNode rootNode;

    //Knowledge base File Object.
    private File knowledgeBaseFile;

    public AnimalAssistant(String fileName) {
        this.fileName = fileName;
    }


    public void loadKnowledgeBase(Scanner kb) {
        LocalTime current = LocalTime.now();

        //Greetings
        if (current.isBefore(LocalTime.of(5, 0))) {
            System.out.println("Hi, Early Bird");
        } else if (current.isBefore(LocalTime.NOON)) {
            System.out.println("Good morning!");
        } else if (current.isBefore(LocalTime.of(18, 0))) {
            System.out.println("Good afternoon");
        } else if (current.isBefore(LocalTime.of(21, 0))) {
            System.out.println("Hi, Night Owl");
        }
        System.out.println();
        try {
            knowledgeBaseFile = new File(fileName);
            Animal guessedAnimal = new Animal();

            //use library to read the JSON file if exists.
            //else Start BinaryTree from scratch with no nodes.
            if (knowledgeBaseFile.exists()) {
                rootNode = new ObjectMapper().readValue(knowledgeBaseFile, BinaryTreeNode.class);
                System.out.println("I know a lot about animals.");
            } else {
//                System.out.println(app.getString("animal.wantLearn"));
                System.out.println("I want to learn about animals.");
                System.out.println("Which animal do you like most?");
                String userFavAnimal = kb.nextLine().toLowerCase().trim();

                //initialize the Animal Object
                initAnimal(guessedAnimal, userFavAnimal);
                System.out.println("Wonderful! I've learned so much about animals!");

                //set the root node to user's favorite animal Object.
                rootNode = new BinaryTreeNode(String.format("Is it %s %s?\n",
                        guessedAnimal.getArticle(), guessedAnimal.getName()), null);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playGame(Scanner kb) {
        try {
            boolean playing = true;

            //Start the guessing game
            while (playing) {
                System.out.println("You think of an animal, and I guess it.");
                System.out.println("Press enter when you're ready.");

                //Let the user press Enter...
                kb.nextLine();
                //Let the computer guess the Animal user might think of.
                //Reuse the kb Scanner object to the method to ask questions to user on
                //animal's characteristics.
                BinaryTreeNode guessedAnimalNode = rootNode.makeGuess(kb);
//                System.out.println("guessedAnimalNode.getValue() = " + guessedAnimalNode.getValue());

                // Initialize the guessed Animal Object.
                Animal guessedAnimal = new Animal();
                initAnimal(guessedAnimal, guessedAnimalNode.getAnimalName());
//                System.out.println("Guessed Animal name=" + guessedAnimal.getName());

                boolean completed = false;
                boolean answerCorrect = false;

                Animal correctAnimal = new Animal();

                do {
                    //read the keyboard input and remove every non-Alpha Numeric Character
                    //as our Sets don't contain character with punctuation marks.
                    //Not sure why I decided to use replaceFirst instead of replaceAll!
                    String answer = kb.nextLine().toLowerCase().trim().replaceFirst("[.?!]", "");
                    if (YES.contains(answer)) {
                        //Set completed true once the program understands the user's input.
                        completed = true;

                        //Program correctly guessed the user's animal.
                        answerCorrect = true;
                    } else if (NO.contains(answer)) {
                        //Set completed true once the program understands the user's input.
                        completed = true;
                        System.out.println("I give up. What animal do you have in mind?");

                        //init correct Animal and let answerCorrect be false.
                        answerCorrect = false;
                        String correctAnimalName = kb.nextLine().toLowerCase().trim();
                        initAnimal(correctAnimal, correctAnimalName);
                    } else {
                        //Randomly choose one of the clarification to output.
                        System.out.println(CLARIFICATIONS.get((int)
                                (Math.random() * CLARIFICATIONS.size())));
                    }
                } while (!completed);

                String fact;

                //reuse the variable.
                completed = false;

                if (!answerCorrect) {
                    do {
                        System.out.printf("Specify a fact that distinguishes %s %s from %s %s.\n" +
                                        "The sentence should satisfy one of the following templates:\n" +
                                        "- It can ...\n" +
                                        "- It has ...\n" +
                                        "- It is a/an ...\n\n", guessedAnimal.getArticle(), guessedAnimal.getName(),
                                correctAnimal.getArticle(), correctAnimal.getName());
                        // Learn facts
                        fact = kb.nextLine().toLowerCase().trim();

                        //parse the fact.
                        if (fact.matches("it (can|has|is).+")) {
                            fact = fact.replaceFirst("it ", "");
                            fact = fact.replaceAll("[.?!]", "");
                            completed = true;
                        } else {
                            System.out.println("The examples of a statement:\n" +
                                    " - It can fly\n" +
                                    " - It has horn\n" +
                                    " - It is a mammal");
                        }
                    } while (!completed);


                    //Identify which animal has the characteristics and which doesn't
                    System.out.printf("Is the statement correct for %s %s?\n",
                            correctAnimal.getArticle(), correctAnimal.getName());
                    completed = false;
                    fact = fact.trim();
                    do {
                        //read the keyboard input and remove every non-Alpha Numeric Character
                        //as our Sets don't contain character with punctuation marks.
                        //Not sure why I decided to use replaceFirst instead of replaceAll!
                        String response = kb.nextLine()
                                .trim()
                                .toLowerCase()
                                .replaceFirst("[.?!]", "");

                        //If yes the fact is true of user-input animal else,
                        // the fact is true for guessed Animal.
                        if (YES.contains(response)) {
//                            System.out.println("You answered: Yes");

                            // Set Animal facts
                            guessedAnimal.addFact(fact, false);
                            correctAnimal.addFact(fact, true);

                            //Add new nodes and get Smarter!!!
                            guessedAnimalNode.setLeftNode(new BinaryTreeNode(
                                    String.format("Is it %s %s?\n",
                                            guessedAnimal.getArticle(), guessedAnimal.getName()),
                                    guessedAnimalNode));

                            guessedAnimalNode.setRightNode(new BinaryTreeNode(
                                    String.format("Is it %s %s?\n",
                                            correctAnimal.getArticle(), correctAnimal.getName()),
                                    guessedAnimalNode));

                            completed = true;
                        } else if (NO.contains(response)) {
//                System.out.println("You answered: No");

                            // Set Animal facts
                            guessedAnimal.addFact(fact, true);
                            correctAnimal.addFact(fact, false);

                            //Add new nodes and get Smarter!!!
                            guessedAnimalNode.setLeftNode(new BinaryTreeNode(
                                    String.format("Is it %s %s?\n",
                                            correctAnimal.getArticle(), correctAnimal.getName()),
                                    guessedAnimalNode));

                            guessedAnimalNode.setRightNode(new BinaryTreeNode(
                                    String.format("Is it %s %s?\n",
                                            guessedAnimal.getArticle(), guessedAnimal.getName()),
                                    guessedAnimalNode));
                            completed = true;
                        } else {
                            System.out.println(CLARIFICATIONS.get((int) (Math.random() * CLARIFICATIONS.size())));
                        }
                    } while (!completed);

                    //Print some useful information.
                    System.out.println("I have learned the following facts about animals:");
                    listFacts(guessedAnimal);
                    listFacts(correctAnimal);
                    System.out.println("I can distinguish these animals by asking the question:");
                    String type = canHasIs(guessedAnimal.listFacts().get(0));
                    String questionToAsk = String.format("- %s%s?\n", type, guessedAnimal.listFacts().get(0)
                            .replaceFirst("(can|has|is)", ""));
                    guessedAnimalNode.setValue(questionToAsk);
                    System.out.print(questionToAsk);
                }

                System.out.println("Wonderful! I've learned so much about animals!");
                System.out.println("Do you like to play again?");
                String playAgain = kb.nextLine().toLowerCase().trim();

                //reuse the variable.
                completed = false;
                do {
                    if (NO.contains(playAgain)) {
                        playing = false;
                        completed = true;
                    } else if (!YES.contains(playAgain)) {
                        System.out.println(CLARIFICATIONS.get((int) (Math.random() * CLARIFICATIONS.size())));
                    } else {
                        completed = true;
                    }

                } while (!completed);
            }

            System.out.println(FAREWELLS.get((int) (Math.random() * FAREWELLS.size())));

            //uses the library to output the knowledge base to a JSON, XML or YAML file(JSON is default)
            ObjectMapper objectMapper = new JsonMapper();
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(knowledgeBaseFile, rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Extracts all the leaves of the binary tree. They are the known animals to this program.
    public void printAllAnimals() {
        List<String> animalNames = rootNode.listLeaves();
        animalNames.sort(Comparator.naturalOrder());
        System.out.println("Here are the animals I know:");
        for (String animalName : animalNames) {
            System.out.printf(" - %s\n", animalName);
        }
    }

    //Extracts all the non-leaves or parent nodes of the binary tree. They are the known facts about certain animals.
    public void printAnimalFacts(String animalName) {
        Optional<BinaryTreeNode> animalNode = rootNode.getAnimalNode(animalName);
        if (animalNode.isPresent()) {
            BinaryTreeNode foundNode = animalNode.get();
            System.out.println("Facts about the " + animalName + ":");
            List<String> parents = foundNode.getParents();
            Collections.reverse(parents);
            for (String fact : parents) {
                System.out.printf(" - %s.\n", fact);
            }
        }
    }

    //Prints some formatted statistics about the knowledge base.
    public void printStatistics() {
        System.out.println("The Knowledge Tree stats");
        System.out.printf(" - root node\t\t\t\t\t\t\t%s\n",
                rootNode.deriveFactFromQuestion(
                        rootNode.getValue().substring(2), rootNode, rootNode.getRightNode()));
        int totalNodesCount = rootNode.getTotalNodesCount();
        int animalsCount = rootNode.listLeaves().size();
        System.out.printf(" - total number of nodes\t\t\t\t%d\n", totalNodesCount);
        System.out.printf(" - total number of animals\t\t\t\t%d\n", animalsCount);
        System.out.printf(" - total number of statements\t\t\t%d\n", totalNodesCount - animalsCount);
        System.out.printf(" - height of the tree\t\t\t\t\t%d\n", BinaryTreeNode.getTreeHeight(rootNode));
        System.out.printf(" - minimum depth\t\t\t\t\t\t%d\n", rootNode.minDepth());
        System.out.printf(" - average depth\t\t\t\t\t\t%f\n", rootNode.getAverageDepth());

        //treeString method has problems in it!!!
        //Does not work as intended.
        System.out.println(rootNode.getTreeString(" └ "));
    }

    // method not completed (yet).
    public void printTree() {
    }

    // returns "an" is word starts with a vowel else
    // "a"
    private String getArticle(String word) {
        if (vowels.contains(word.substring(0, 1))) {
            return "an";
        } else {
            return "a";
        }
    }

    // checks if the word is a article("a" or "an").
    private boolean isArticle(String article) {
        return article.equalsIgnoreCase("a") || article.equalsIgnoreCase("an");
    }

    private void initAnimal(Animal animal, String input) {
        String[] inputArray = input.split("\\s+");
        if (inputArray.length == 1) {
            animal.setName(input);
            animal.setArticle(getArticle(animal.getName()));
        } else {
            animal.setName(inputArray[1]);
            animal.setArticle(inputArray[0]);
//            System.out.println(Arrays.toString(inputArray));
            if (!isArticle(animal.getArticle())) {
                animal.setArticle(getArticle(animal.getName()));
                animal.setName(input);
            }
        }
    }

    private void listFacts(Animal animal) {
        List<String> parsedAnimalFacts = animal.getFacts();
        for (String parsedFact : parsedAnimalFacts) {
            System.out.printf("- The %s %s.\n", animal.getName(), parsedFact);
        }
    }

    private static String canHasIs(String fact) {
        if (fact.toLowerCase().contains("can")) {
            return "Can it";
        } else if (fact.toLowerCase().contains("has")) {
            return "Does it have";
        } else {
            return "Is it";
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
