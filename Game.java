import java.io.*;
import java.util.*;
/**
 * Kevin Bacon game implementation on entire data set
 * @author Yakoob Khan & Aadil Islam
 */

public class Game {

	public static void main(String[] args) throws IOException {
		// Load the file to get maps of  id -> movies and id -> actor 
		Map <String, String> movies = GraphLib.id2Name("inputs/movies.txt");
		Map <String, String> actors =  GraphLib.id2Name("inputs/actors.txt");
		Map <String, List<String>> movie2Actors = GraphLib.movie2Actors("inputs/movie-actors.txt", actors, movies);
		
		// Build the graph
		Graph<String,Set<String>> g = GraphLib.buildGraph(actors, movie2Actors);
		
		
		// Boilerplate code of instructions for playing the game 
		System.out.println("Commands:\n" + "c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" + 
				"d <low> <high>: list actors sorted by degree, with degree between low and high\n" + 
				"i: list actors with infinite separation from the current center\n" + 
				"p <name>: find path from <name> to current center of the universe\n" + 
				"s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" + 
				"u <name>: make <name> the center of the universe\n" + 
				"q: quit game");
		// Take user input through scanner
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		// User_input stores info entered by user, command stores the first letter, response contains everything else
		String user_input, command, response;
		// Set Kevin Bacon to be the center of the universe
		String center = "Kevin Bacon";
		// Create path tree with Kevin Bacon as the center
		Graph<String, Set<String>> tree = GraphLib.bfs(g, center);
		System.out.println("\n" + center + " is now the center of the acting universe, connected to " + 
		(g.numVertices() - GraphLib.missingVertices(g, tree).size()-1) + "/" + g.numVertices() + 
		" actors with average separation " + GraphLib.averageSeparation(tree, center));
		
		// Use a while loop to play the game
		while (true) {
			System.out.println("\n" + center + " game >");
			// Save user input 
			user_input = input.nextLine();
			// Parse user input
			if(user_input.contains(" ")) {	// if there is a space
				command = user_input.substring(0,user_input.indexOf(' '));	// command is the first letter 
				response = user_input.substring(user_input.indexOf(' ') + 1);	// response is anything that comes after the space of first letter
			}
			else {
				command = user_input;
				response = center;
			}
			// Execute the relevant block of code according to user input
			// Press 'q' To quit the game
			if(command.equals("q")) {	
				return;
			}
			//Press 'p' and give vertex name to find path to the center of the universe
			else if (command.equals("p")) {	
				if(tree.hasVertex(response)) {	// check that the vertex entered is in the path tree
					List<String> path = GraphLib.getPath(tree, response);		// retrive path of chosen actor to center
					// Print the actor's distance from center of the universe (its Kevin Bacon number)
					System.out.println(path.get(0).toString() + " 's number is " + (path.size()-1));
					// Print the series of actors that leads to the center actor
					for (int i = 0; i <path.size()-1; i++) {
						System.out.println(path.get(i) + " appeared in " + g.getLabel(path.get(i), path.get(i+1))  + " with " + path.get(i+1));		
					}
				}
				else { // Request user to enter the info in the correct format
					System.out.println("Please enter a valid vertex");
				}
			}
			// List top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
			else if (command.equals("c")) {	
				int num = Integer.parseInt(response);
				if (Math.abs(num) > g.numVertices()) {		// check that number entered by user is with the number of vertices of the graph
					System.out.println("Please choose a valid size");
				}
				else { 
					Map<String, Double> vert2AvgSep = new HashMap<String, Double>();
					for (String vertex: tree.vertices()) {		// Loop over all the vertices
						Graph<String, Set<String>> tempTree = GraphLib.bfs(g, vertex);	// create a path tree with this vertex as center
						Double avgSep = GraphLib.averageSeparation(tempTree, vertex);	// get the average separation
						vert2AvgSep.put(vertex, avgSep);			// add this to the map
					}
					List<String> res = new ArrayList<String>();	// stores the vertex names
					
					if (num != 0){ 	// ensure num is not zero
						PriorityQueue<String> pq; 
						if (num < 0) {	// use Minimum PQ if number entered is negative
							pq = new PriorityQueue<String>((String v1, String v2) -> (int)((1000)* vert2AvgSep.get(v1) - (1000)* vert2AvgSep.get(v2)));
						}
						else {		// use Maximum PQ if number entered is positive
							pq = new PriorityQueue<String>((String v1, String v2) -> (int)((1000)* vert2AvgSep.get(v2) - (1000)* vert2AvgSep.get(v1)));	
						}
						for (String vertex: vert2AvgSep.keySet() ) {	// Loop over the vertices
							pq.add(vertex);
						}
						// Remove from pq and add it to the results array
						for (int i =0; i<Math.abs(num); i++) res.add(pq.remove());		
					}
					System.out.println(res);		// print the result array
				}
			}
			// d <low> <high>: list actors sorted by degree, with degree between low and high
			else if (command.equals("d")) {
				// Check if info entered is in the valid format
				if (Character.isDigit(response.charAt(0)) && Character.isDigit(response.charAt(2)) && response.charAt(0) < response.charAt(2))  {
					
					String[] lowHigh = response.split(" "); 		// store the low and high numbers
					// Get a list of actors sorted by decending in-degree
					List<String> actorsByDegree = GraphLib.verticesByInDegree(tree);
					Collections.reverse(actorsByDegree);		// reverse the list to ascending order
					
					int low = Integer.parseInt(lowHigh[0]);	// low and high stores the parsed characters
					int high = Integer.parseInt(lowHigh[1]);
					
					List<String> res = new ArrayList<String>();	// stores vertices with low and high
					for(int x=0; x<actorsByDegree.size(); x++) {		//loop over all the vertices in ascending order
						// Check if it is within the range
						if(g.inDegree(actorsByDegree.get(x)) >= low && g.inDegree(actorsByDegree.get(x)) <= high) {
							res.add(actorsByDegree.get(x));
						}	
					}
					Collections.reverse(res);		// reverse the results to sort them in low -> high
					System.out.println(res);
				}	
				else {	// Asks user to enter info in valid format
					System.out.println("Please enter the information in the valid format");
				}
			}
			// i: list actors with infinite separation from the current center
			else if (command.equals("i")) {
				System.out.println(GraphLib.missingVertices(g, tree));	// just print the vertices not in the path tree
			}
			else if (command.equals("s")) {
				// Check if info entered is in the correct format
				if (Character.isDigit(response.charAt(0)) && Character.isDigit(response.charAt(2)) && response.charAt(0) < response.charAt(2)) {
					String[] lowHigh = response.split(" "); 		// store the low and high numbers
					
					int low = Integer.parseInt(lowHigh[0]);
					int high = Integer.parseInt(lowHigh[1]);
					Map<String, Integer> vert2Sep = new HashMap<String, Integer>();
					for (String vertex: tree.vertices()) {	// loop over all the vertices in the path tree
						List<String> path = GraphLib.getPath(tree, vertex);	// obtain the path
						Integer avgSep = (Integer)(path.size()-1);			// get the path size
						vert2Sep.put(vertex, avgSep);	// put it in the Map
					}
					// Create minimum priority queue
					PriorityQueue<String> pq = new PriorityQueue<String>((String actor1, String actor2) -> vert2Sep.get(actor1) - vert2Sep.get(actor2));
					for (String vertex: vert2Sep.keySet()) {		// loop over the map
						if (vert2Sep.get(vertex) >= low && vert2Sep.get(vertex) <= high) {	// if within the range, add it to the priority queue
							pq.add(vertex);
						}
					}
					System.out.println(pq);		// print the priority queue
				}	
				else {		// Asks user to enter information in the valid format
					System.out.println("Please enter the information in the valid format");
				}
			}
			// u <name>: make <name> the center of the universe
			else if(command.equals("u")) {	// change the center of the universe
				// Check if the vertex entered is in the graph
				if(!g.hasVertex(response)) System.out.println("Please enter a valid center vertex");
				// If vertex entered is not in the current tree
				if (!tree.hasVertex(response) && g.hasVertex(response)) {
					tree = GraphLib.bfs(g, response);	// create new tree
					center = response;		// set response to be the new center
				} // Vertex entered is in the tree
				if(g.hasVertex(response) && tree.hasVertex(response)) {
					System.out.println(response + " is now the center of the acting universe, connected to "
				+ (g.numVertices() - GraphLib.missingVertices(g, tree).size()-1) + "/" + g.numVertices() 
				+ " actors with average separation " + GraphLib.averageSeparation(tree, response));
					center = response;	// create new tree
					tree = GraphLib.bfs(g, center); // set response to be the new center
				}
			}
			// Asks user to enter a valid command
			else {
				System.out.println("Please enter a valid command");
			}
		}
	}
}
