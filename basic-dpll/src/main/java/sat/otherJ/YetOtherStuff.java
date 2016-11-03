package sat.otherJ;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by privatejw on 4/11/2016.
 */

public class YetOtherStuff {
    String directory = "D://SUTD Term 4//50 .002 - LI01 Computation Structures//2D cnf//SATSOLVER//basic-dpll//src//main//java//";
    String fname = "jonprob.cnf";
    int numVars;
    ArrayList<ArrayList<String>> currentList;
    ArrayList<String> clause;
    ArrayList<String> currVars;
    boolean neg;
    boolean clauseSatisfied;

    HashMap<String, Boolean> varVals = new HashMap<>();
    ArrayList<ArrayList<String>> readFile(String filename) {

        ArrayList<ArrayList<String>> fList = new ArrayList<>();
        ArrayList<String> clauseList;
        String line;
        try {
            FileInputStream fis = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            while ((line = br.readLine()) != null) {
                clauseList = new ArrayList<String>();
                if (line.length() != 0) {
                    String[] subList = line.split("\\s+");
                    if (subList[0].equals("p")){
                        numVars = Integer.parseInt(subList[2]);
                    } else if (!subList[0].equals("c")){
                        for (String i : subList){
                            if (!i.equals("0")){
                                clauseList.add(i);
                            }
                        }
                        fList.add(new ArrayList<String>(clauseList));
                    }

                }
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fList;
    }

    void runMe() {
        currentList = readFile(directory + fname);

        Node rootNode = new Node(currentList.get(0).get(0).replaceFirst("-",""), false) ;
        varVals.put(rootNode.name, false);
        Node currNode = rootNode;

        for (int i=0; i < currentList.size(); i++) {
            clause = currentList.get(i);
            currVars = new ArrayList<>();
            clauseSatisfied = false;

            for (String variable: clause) {
                neg = false;
                if (variable.matches("-.*")) {
                    neg = true;
                    variable = variable.substring(1);
                }
                currVars.add(variable);
                if (!varVals.containsKey(variable)) {
                    varVals.put(variable, !neg);
                    if (currNode.value && currNode.right == null) {
                        Node n  = new Node(variable, !neg);
                        n.parent = currNode;
                        currNode.right = n;
                        currNode = n;
                    } else if (!currNode.value && currNode.left == null) {
                        Node n  = new Node(variable, !neg);
                        n.parent = currNode;
                        currNode.left = n;
                        currNode = n;
                    }
                    clauseSatisfied = true;
                    break;
                } else {
                    if ((neg && !varVals.get(variable)) || (!neg && varVals.get(variable))) {
                        clauseSatisfied = true;
                        break;
                    }
                }
            }
            if (!clauseSatisfied) {
                i = -1;
                // begin backtracking
                boolean conflictFound = false;
                while (!conflictFound) {
                    varVals.remove(currNode.name);
                    currNode = currNode.parent;
                    if (currNode == null) {
                        // solution not found
                        System.out.println("UNSAT");
                        return;
                    }
                    if (currVars.contains(currNode.name)) {
                        if (currNode.left != null || currNode.right != null)
                            conflictFound = true;
                    }
                }
                if (currNode.left == null) {
                    currNode.value = false;
                    varVals.put(currNode.name, false);
                } else if (currNode.right == null) {
                    currNode.value = true;
                    varVals.put(currNode.name, true);
                }
            }
        }
        System.out.println("SAT");

        System.out.println(varVals);
    }

    private class Stack {
        ArrayList<String> s = new ArrayList<>();
        void add(String var) {
            s.add(var);
        }
        void pop() {}
    }

    private class Node {
        Node parent;
        String name;
        boolean value;
        Node left;
        Node right;
        Node(String name, boolean value) {
            this.name = name;
            this.value = value;
        }
        void setLeft(Node n) {
            this.left = n;
        }
        void setRight(Node n) {
            this.right = n;
        }
    }

    public static void main(String[] args) {
        (new YetOtherStuff()).runMe();
    }
}

