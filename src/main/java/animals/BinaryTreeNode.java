package animals;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

//A class for representing a full Binary tree
//A full Binary tree is a tree whose nodes that are not a leaf has to have exactly 2 child nodes.
public class BinaryTreeNode {
    //The value of the this Node
    //Can be the name of a animal if this node is leaf, else
    //it is a yes/no question.
    private String value;

    //Node if answered No
    private BinaryTreeNode leftNode;

    //Node if answered yes.
    private BinaryTreeNode rightNode;

    //parent node of this node.
    //Don't save it in Json, as parent node's left/right node is this node
    //and can lead to memory/stack overflow error.
    @JsonIgnore
    private BinaryTreeNode parent;

    public BinaryTreeNode() {
    }

    public BinaryTreeNode(String value, BinaryTreeNode leftNode, BinaryTreeNode rightNode) {
        this.value = value;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    public BinaryTreeNode(String value, BinaryTreeNode parentNode) {
        this.value = value;
        this.parent = parentNode;
    }

    @JsonIgnore
    public List<String> listLeaves() {
        if (leftNode == null) {
            if (rightNode == null) {
                return List.of(getAnimalName());
            } else {
                System.out.println("ERROR");
                System.out.println("If left node is null right node has to be NULL!");
                throw new RuntimeException();
            }
        } else {
            List<String> leftLeaves = leftNode.listLeaves();
            List<String> rightLeaves = rightNode.listLeaves();
            List<String> allLeaves = new ArrayList<>();
            allLeaves.addAll(leftLeaves);
            allLeaves.addAll(rightLeaves);
            return allLeaves;
        }
    }

    @JsonIgnore
    public String getAnimalName() {
        return value
                .replace("Is it a ", "")
                .replace("?", "")
                .replace("\n", "");
    }

    @JsonIgnore
    public double getAverageDepth() {
        List<String> animalNames = listLeaves();
        return (double) totalDepth(animalNames) / animalNames.size();
    }

    @JsonIgnore
    public int totalDepth(List<String> animalNames) {
        int sum = 0;
        for (String nodeValue : animalNames) {
            sum += getNodeDepthRec(nodeValue);
//            System.out.println(nodeValue);
//            System.out.println(sum);
        }
        return sum;
    }

    @JsonIgnore
    public int getNodeDepthRec(String nodeValue) {
        if (this.getAnimalName().equalsIgnoreCase(nodeValue)) {
            return 0;
        }
        if (leftNode == null) {
            if (rightNode == null) {
                return Integer.MIN_VALUE;
            } else {
                return rightNode.getNodeDepthRec(nodeValue) + 1;
            }
        } else {
            if (rightNode == null) {
                return leftNode.getNodeDepthRec(nodeValue) + 1;
            } else {
                return Math.max(leftNode.getNodeDepthRec(nodeValue), rightNode.getNodeDepthRec(nodeValue)) + 1;
            }
        }
    }

    @JsonIgnore
    public int getNodeDepth(String nodeValue) {
        Deque<BinaryTreeNode> treeNodes = new LinkedList<>();
        treeNodes.push(this);
        int depth = 0;
        while (!treeNodes.isEmpty()) {
            BinaryTreeNode currentNode = treeNodes.peek();
            if (currentNode.leftNode == null) {
                if (currentNode.rightNode == null) {
                    if (treeNodes.pop().getAnimalName().equalsIgnoreCase(nodeValue)) {
                        return depth;
                    } else {
                        depth--;
                    }
                } else {
                    System.out.println("adding " + currentNode.rightNode.value);
                    treeNodes.push(currentNode.rightNode);
                    depth++;
                }
            } else {
                if (currentNode.rightNode != null) {
                    System.out.println("adding " + currentNode.rightNode.value);
                    treeNodes.push(currentNode.rightNode);
//                    assert treeNodes.peek() != null;
                }
                System.out.println("adding " + currentNode.leftNode.value);
                treeNodes.push(currentNode.leftNode);
                depth++;
            }
        }
        return depth;
    }

    @JsonIgnore
    public int minDepth() {
        if (leftNode == null) {
            return 0;
        }
        return Math.min(leftNode.minDepth() + 1, rightNode.minDepth() + 1);
    }

    @JsonIgnore
    public static int getTreeHeight(BinaryTreeNode rootNode) {
        if (rootNode.leftNode == null) {
            return 0;
        }
        return Math.max(getTreeHeight(rootNode.leftNode) + 1, getTreeHeight(rootNode.rightNode) + 1);
    }

    @JsonIgnore
    public int getTotalNodesCount() {
        if (leftNode == null) {
            return 1;
        }
        return leftNode.getTotalNodesCount() + rightNode.getTotalNodesCount() + 1;
    }

    // Gets all the non leaf nodes
    @JsonIgnore
    public List<String> getParents() {
        BinaryTreeNode parentNode = parent;
        BinaryTreeNode currentNode = this;
        List<String> facts = new ArrayList<>();
        while (parentNode != null) {
            String question = parentNode.getValue();
            String fact =
                    deriveFactFromQuestion(question.substring(2), parentNode, currentNode);

            facts.add(fact);
            currentNode = parentNode;
            parentNode = parentNode.parent;
        }
        return facts;

    }

    // Does not work as intended.
    @JsonIgnore
    public String getTreeString(String startString) {

        if (rightNode.leftNode == null) {
            String animalName1 = rightNode.value.replace("Is it ", "");
            String animalName2 = leftNode.value.replace("Is it ", "");
            return startString + rightNode.value
                    + " " + startString + animalName1
                    + " " + startString + animalName2;
        } else {
            String string = startString + value;
            string += rightNode.getTreeString(" " + startString);
            string += leftNode.getTreeString(" " + startString);
            return string;
        }
    }

    // return a readable fact from question in parentNode
    // Negative response if the currentNode
    // Method requires some reword as == operator is used for equality testing.
    public String deriveFactFromQuestion(String question, BinaryTreeNode parentNode, BinaryTreeNode currentNode) {
        String fact;
        if (question.startsWith("Can it")) {
            String canIt = removeQuestionAndNewLine(question
                    .replace("Can it", ""));
            if (parentNode.leftNode == currentNode) {
                fact = "It can't" + canIt;
            } else {
                fact = "It can" + canIt;
            }
        } else if (question.startsWith("Does it have")) {
            String doesItHave = removeQuestionAndNewLine(question
                    .replace("Does it have", ""));
            if (parentNode.leftNode == currentNode) {
                fact = "It doesn't have" + doesItHave;
            } else {
                fact = "It has" + doesItHave;
            }

        } else if (question.startsWith("Is it")) {
            String isIt = removeQuestionAndNewLine(question
                    .replace("Is it", ""));
            if (parentNode.leftNode == currentNode) {
                fact = "It isn't" + isIt;
            } else {
                fact = "It is" + isIt;
            }
        } else {
            System.out.println("Unable to parse question!");
//            System.out.println(question);
            throw new UnknownFormatConversionException(question);
        }
        return fact;
    }

    private String removeQuestionAndNewLine(String string) {
        return string.replace("?", "").replace("\n", "");
    }

    //Uses Pre-Order Traversal for only leaves.
    public Optional<BinaryTreeNode> getAnimalNode(String animalName) {
        if (leftNode == null) {
            // assert leftNode == null
            // assert rightNode == null
            if (rightNode == null) {
                if (animalName.contentEquals(getAnimalName())) {
                    return Optional.of(this);
                } else {
                    return Optional.empty();
                }
            } else {
                System.out.println("ERROR");
                System.out.println("If left node is null right node has to be NULL!");
                throw new IllegalStateException("If left node is null right node has to be NULL!");
            }
        } else {
            //Pre-Order Traversal, check the leftMostNode first and rightMost Node last.
            // and since we only traverse the leaves we don't check this node as this node has children
            // and therefore is not a leaf
            Optional<BinaryTreeNode> optionalLeft = leftNode.getAnimalNode(animalName);
            Optional<BinaryTreeNode> optionalRight = rightNode.getAnimalNode(animalName);
            if (optionalLeft.isPresent()) {
                return optionalLeft;
            } else {
                return optionalRight;
            }
        }
    }

    @JsonIgnore
    public BinaryTreeNode makeGuess(Scanner kb) {
//        System.out.println(toString());
        //ask a question or guess animal name.
        System.out.println(value);

        //if a node has a left node it has to have a right node.
        if ((leftNode == null) != (rightNode == null)) {
            throw new IllegalStateException("Only one of the node is null. Has to be a Full binary tree");
        }

        //if this is not a leaf
        //right node is always not null because in previous if (leftNode == null) != (rightNode == null)
        //the method throws an exception.
        if (leftNode != null/* && rightNode != null*/) {

            //forever loop to ensure that the input is parsed correctly.
            while (true) {
                //read the keyboard input and remove every non-Alpha Numeric Character
                //as our Sets don't contain character with punctuation marks.
                //Not sure why I decided to use replaceFirst instead of replaceAll!
                String response = kb.nextLine().toLowerCase().trim()
                        .replaceFirst("[.?!]", "");
                //if yes, look at the right node
                //else look at the left node
                if (AnimalAssistant.YES.contains(response)) {
                    return rightNode.makeGuess(kb);
                } else if (AnimalAssistant.NO.contains(response)) {
                    return leftNode.makeGuess(kb);
                } else {
                    //Randomly choose one of the clarification to output.
                    System.out.println(AnimalAssistant.CLARIFICATIONS.get((int)
                            (Math.random() * AnimalAssistant.CLARIFICATIONS.size())));
                }


            }
        } else {
            //return this when this node becomes the leaf node.
            //The leaf node is also the node which store animal name.
            return this;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BinaryTreeNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(BinaryTreeNode leftNode) {
        this.leftNode = leftNode;
    }

    public BinaryTreeNode getRightNode() {
        return rightNode;
    }

    public void setRightNode(BinaryTreeNode rightNode) {
        this.rightNode = rightNode;
    }

    public BinaryTreeNode getParent() {
        return parent;
    }

    public void setParent(BinaryTreeNode parent) {
        this.parent = parent;
    }

    //toString is erroneous. Not yet implemented as desired!
    @Override
    public String toString() {
        if (leftNode != null) {
            if (rightNode != null) {
                return value + "->" + leftNode.value + ", " + rightNode.value + "\n"
                        + leftNode
                        + rightNode;
            } else {
                return value + "->" + leftNode.value + ", " + "(none)" + "\n"
                        + leftNode;
            }
        } else if (rightNode != null) {
            return value + "->" + "(none)" + ", " + rightNode.value + "\n"
                    + rightNode;
        } else {
            return "";
        }

    }
}
