package games;

import java.util.ArrayList;


/**
 * This class contains methods to represent the Hunger Games using BSTs.
 * Moves people from input files to districts, eliminates people from the game,
 * and determines a possible winner.
 * 
 * @author Pranay Roni
 * @author Majsims jurjanovics jravcenjo
 * @author jal Pandit
 */
public class HungerGames {

    private ArrayList<District> districts;  // all districts in Panem.
    private TreeNode            game;       // root of the BST. The BST contains districts that are still in the game.

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Default constructor, initializes a list of districts.
     */
    public HungerGames() {
        districts = new ArrayList<>();
        game = null;
        StdRandom.setSeed(2023);
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * Sets up Panem, the universe in which the Hunger Games tajes place.
     * Reads districts and people from the input file.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupPanem(String filename) { 
        StdIn.setFile(filename);  // open the file - happens only once here
        setupDistricts(filename); 
        setupPeople(filename);
    }

    /**
     * Reads the following from input file:
     * - Number of districts
     * - District ID's (insert in order of insertion)
     * Insert districts into the districts ArrayList in order of appearance.
     * 
     * @param filename will be provided by client to read from using StdIn
     */
    public void setupDistricts (String filename) {
        // WRITE YOUR CODE HERE
        StdIn.setFile(filename);
        int Districts= StdIn.readInt();
        for (int i = 0; i < Districts; i++) {
            int districtID = StdIn.readInt();
            District district = new District(districtID);
            districts.add(district);
        }
    }

    /**
     * Reads the following from input file (continues to read from the SAME input file as setupDistricts()):
     * Number of people
     * Space-separated: first name, last name, birth month (1-12), age, district id, effectiveness
     * Districts will be initialized to the instance variable districts
     * 
     * Persons will be added to corresponding district in districts defined by districtID
     * 
     * @param filename will be provided by client to read from using StdIn
     */

     
    public void setupPeople (String filename) {
        // WRITE YOUR CODE HERE
        StdIn.setFile(filename);
        int numDistricts = Integer.parseInt(StdIn.readLine());
        int[] districtIDs = new int[numDistricts];
        for (int i = 0; i < numDistricts; i++){
            districtIDs[i]= Integer.parseInt(StdIn.readLine());
        }
        String[] peopleData = StdIn.readAllLines();
        int numPeople = Integer.parseInt(peopleData[0]);
        for (int i = 1; i <= numPeople; i++) {
            String[] person =peopleData[i].split(" ");
            Person newPerson= createPerson(person);
            addPersonToDistrict(newPerson, districtIDs);
        }
    }
    private Person createPerson(String[] person){
        int birthMonth = Integer.parseInt(person[2]);
        String firstName= person[0];
        String lastName =person[1];
        int age =Integer.parseInt(person[3]);
        int districtID = Integer.parseInt(person[4]);
        int effectiveness = Integer.parseInt(person[5]);
        Person newPerson = new Person(birthMonth, firstName, lastName, age, districtID, effectiveness);
        if (age >= 12 &&age <18){
            newPerson.setTessera(true);
        } else{
            newPerson.setTessera(false);
        }
        return newPerson;
    }
    private void addPersonToDistrict(Person person, int[] districtIDs) {
        for (int i = 0; i < districtIDs.length; i++){
            if(districts.get(i).getDistrictID() == person.getDistrictID()) {
                if(person.getBirthMonth()% 2 ==0){
                    districts.get(i).addEvenPerson(person);
                }else{
                    districts.get(i).addOddPerson(person);
                }
            }
        }
    }

    /**
     * Adds a district to the game BST.
     * If the district is already added, do nothing
     * 
     * @param root        the TreeNode root which we access all the added districts
     * @param newDistrict the district we wish to add
     */
    
     public void addDistrictToGame(TreeNode root, District newDistrict) {
        // WRITE YOUR CODE HERE
        if(root ==null){
            game = new TreeNode(newDistrict, null, null);
            return;
        }
        int compare= newDistrict.getDistrictID()- root.getDistrict().getDistrictID();
        if(compare <0){
            if (root.getLeft()== null) {
                root.setLeft(new TreeNode(newDistrict, null, null));
             }else{
                addDistrictToGame(root.getLeft(), newDistrict);
            }
        }
         if(compare > 0) {
            if (root.getRight() == null) {
                root.setRight(new TreeNode(newDistrict, null, null));
            } else {
                addDistrictToGame(root.getRight(), newDistrict);
            }
        }
    }
    

    /**
     * Searches for a district inside of the BST given the district id.
     * 
     * @param id the district to search
     * @return the district if found, null if not found
     */
    private District findDistrict(TreeNode node, int id) {
        if (node== null){
            return null;
        }
        int cur =node.getDistrict().getDistrictID();
        if (id ==cur){
            return node.getDistrict();
        } 
        if (id< cur){
            return findDistrict(node.getLeft(), id);
        } else{
            return findDistrict(node.getRight(), id);
        }
    }
    public District findDistrict(int id) {
        // WRITE YOUR CODE HERE
        TreeNode ptr = game;
        return findDistrict(ptr, id); // update this line
    }
  

    /**
     * Selects two duelers from the tree, following these rules:
     * - One odd person and one even person should be in the pair.
     * - Dueler with Tessera (age 12-18, use tessera instance variable) must be
     * retrieved first.
     * - Find the first odd person and even person (separately) with Tessera if they
     * ejist.
     * - If you can't find a person, use StdRandom.uniform(j) where j is the respective 
     * population size to obtain a dueler.
     * - Add odd person dueler to person1 of new DuelerPair and even person dueler to
     * person2.
     * - People from the same district cannot fight against each other.
     * 
     * @return the pair of dueler retrieved from this method.
     */
    public DuelPair selectDuelers() {
        // WRITE YOUR CODE HERE
        Person person1 =selectPerson(game, -1, true);
        Person person2;
        if (person1== null) {
            person2 =selectPerson(game, -1, false);
        } else{
            person2 = selectPerson(game, person1.getDistrictID(), false);
        }
        if (person1 == null) {
            if (person2 == null) {
                person1 =selectRandomPerson(game, -1, true);
            } else {
                person1 = selectRandomPerson(game, person2.getDistrictID(), true);
            }
        }
        if (person2== null) {
            if (person1 ==null) {
                person2= selectRandomPerson(game, -1, false);
            } else {
                person2 =selectRandomPerson(game, person1.getDistrictID(), false);
            }
        }
        DuelPair pair= new DuelPair(person1, person2);
        if (person1!= null) {
            removePersonFromDistrict(person1);
        }
        if (person2 != null) {
            removePersonFromDistrict(person2);
        }
            return pair; // update this line
        }
        private void removePersonFromDistrict(Person person) {
            int districtID= person.getDistrictID();
            District district = findDistrict(districtID);
            if (district !=null) {
                ArrayList<Person> oddPopulation = district.getOddPopulation();
                ArrayList<Person> evenPopulation = district.getEvenPopulation();
                if (oddPopulation.contains(person)) {
                    oddPopulation.remove(person);
                    district.setOddPopulation(oddPopulation);
                } else if (evenPopulation.contains(person)) {
                    evenPopulation.remove(person);
                    district.setEvenPopulation(evenPopulation);
                }
            }
        }
        private Person selectPerson(TreeNode node, int id, boolean isOdd) {
            if (node ==null){
                return null;
            }
            ArrayList<Person>population = (isOdd)? node.getDistrict().getOddPopulation(): node.getDistrict().getEvenPopulation();
            for (int i = 0; i <population.size(); i++) {
                if (population.get(i).getTessera() && node.getDistrict().getDistrictID()!= id) {
                    return population.get(i);
                }
            }
            Person personL= selectPerson(node.getLeft(), id, isOdd);
            if (personL!= null) {
                return personL;
            }
            Person personR = selectPerson(node.getRight(), id, isOdd);
            if (personR != null) {
                return personR;
            }
            return null;
        }
        private Person selectRandomPerson(TreeNode node, int id, boolean isOdd) {
            if (node == null){
                return null;
            }
            ArrayList<Person>population =(isOdd) ? node.getDistrict().getOddPopulation() :  node.getDistrict().getEvenPopulation();
            int randomIndex= StdRandom.uniform(population.size());
            Person randomized= population.get(randomIndex);
            if (randomized.getDistrictID() == id) {
                Person randomL =selectRandomPerson(node.getLeft(), id, isOdd);
                if (randomL != null) {
                    return randomL;
                }
                Person randomR = selectRandomPerson(node.getRight(), id, isOdd);
                if (randomR != null) {
                    return randomR;
                }
            } else {
                return randomized;
            }
            return null;
        }

    /**
     * Deletes a district from the BST when they are eliminated from the game.
     * Districts are identified by id's.
     * If district does not ejist, do nothing.
     * 
     * This is similar to the BST delete we have seen in class.
     * 
     * @param id the ID of the district to eliminate
     */
    public void eliminateDistrict(int id) {
        // WRITE YOUR CODE HERE
        game = eliminateDistrict(game, id);
    }
    private TreeNode eliminateDistrict(TreeNode root, int id) {
        if (root ==null) {
            return null;
        }
        if (id <root.getDistrict().getDistrictID()) {
            root.setLeft(eliminateDistrict(root.getLeft(), id));
        } 
        if (id> root.getDistrict().getDistrictID()) {
            root.setRight(eliminateDistrict(root.getRight(), id));
        } else {
            if (root.getLeft()== null) {
                return root.getRight();
            } 
            if (root.getRight() ==null) {
                return root.getLeft();
            } else {
                TreeNode[] right =Delete(root.getRight());
                TreeNode successor= right[0];
                TreeNode updatedRight =right[1];
                root.setDistrict(successor.getDistrict());
                root.setRight(updatedRight);
            }
        }
        return root;
    }
    private TreeNode[]Delete(TreeNode node) {
        if (node.getLeft()== null) {
            TreeNode updatedRight = node.getRight();
            node.setRight(null); // Disconnect the smallest node
            return new TreeNode[]{ node,updatedRight };
        }
        TreeNode[] result =Delete(node.getLeft());
        TreeNode smallestNode= result[0];
        TreeNode updatedRight =result[1];
        node.setLeft(smallestNode);
        return new TreeNode[]{ smallestNode, updatedRight };
    }

    /**
     * Eliminates a dueler from a pair of duelers.
     * - Both duelers in the DuelPair argument given will duel
     * - Winner gets returned to their District
     * - Eliminate a District if it only contains a odd person population or even
     * person population
     * 
     * @param pair of persons to fight each other.
     */
    public void eliminateDueler(DuelPair pair) {
        // WRITE YOUR CODE HERE
        if (pair.getPerson1()!= null && pair.getPerson2()!= null) {
            handleDuelOutcome(pair.getPerson1(),pair.getPerson2());
        } else {
        handleSinglePerson(pair.getPerson1());
        handleSinglePerson(pair.getPerson2());
        }
    }
    private void handleDuelOutcome(Person per1, Person per2) {
        Person winner = per1.duel(per2);
        if (findDistrict(game, winner.getDistrictID()) != null) {
            if (winner.getBirthMonth() % 2 == 1) {
                findDistrict(game, winner.getDistrictID()).addOddPerson(winner);
            } else {
                findDistrict(game, winner.getDistrictID()).addEvenPerson(winner);
            }
        }
        int loser =(winner == per1)? per2.getDistrictID() : per1.getDistrictID();
        if (findDistrict(game, loser) != null) {
            int odd = findDistrict(game, loser).getOddPopulation().size();
            int even =findDistrict(game, loser).getEvenPopulation().size();
            if (odd== 0|| even == 0) {
                eliminateDistrict(loser);
            }
        }
    }
    private void handleSinglePerson(Person person) {
        if (person!= null) {
            int personDistrictID =person.getDistrictID();
            if (findDistrict(game, personDistrictID)!= null) {
                if (person.getBirthMonth()% 2 == 1) {
                    findDistrict(game, personDistrictID).addOddPerson(person);
                } else {
                    findDistrict(game, personDistrictID).addEvenPerson(person);
                }
            }
        }
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Obtains the list of districts for the Driver.
     * 
     * @return the ArrayList of districts for selection
     */
    public ArrayList<District> getDistricts() {
        return this.districts;
    }

    /**
     * ***** DO NOT REMOVE OR UPDATE this method *********
     * 
     * Returns the root of the BST
     */
    public TreeNode getRoot() {
        return game;
    }
}
