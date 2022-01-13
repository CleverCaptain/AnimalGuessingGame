package animals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animal {
    private String article;
    private String name;
    private Map<String, Boolean> facts;

    public Animal() {
        facts = new HashMap<>();
    }

    public Animal(String article, String name) {
        this.article = article;
        this.name = name;
        facts = new HashMap<>();
    }

    public void addFact(String fact, boolean isTrue) {
        facts.put(fact, isTrue);
    }

    public List<String> getFacts() {

        List<String> parsedFacts = new ArrayList<>();
        for (Map.Entry<String, Boolean> factsEntry : facts.entrySet()) {
            if (factsEntry.getValue()) {
                parsedFacts.add(factsEntry.getKey());
            } else {
                if (factsEntry.getKey().startsWith("can")) {
                    parsedFacts.add(factsEntry.getKey().replaceFirst("can", "can't"));
                } else if (factsEntry.getKey().startsWith("has")) {
                    parsedFacts.add(factsEntry.getKey().replaceFirst("has", "doesn't have"));
                } else if (factsEntry.getKey().startsWith("is")) {
                    parsedFacts.add(factsEntry.getKey().replaceFirst("is", "isn't"));
                } else {
                    System.out.println("ERROR");
                }
            }
        }
        return parsedFacts;
    }

    public List<String> listFacts() {
        List<String> parsedFacts = new ArrayList<>();
        for (Map.Entry<String, Boolean> factsEntry : facts.entrySet()) {
            parsedFacts.add(factsEntry.getKey());
        }
        return parsedFacts;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
