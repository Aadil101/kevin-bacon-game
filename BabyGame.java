import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Test mini version of Kevin Bacon Game
 * @author Aadil Islam, Spring 2018
 */

public class BabyGame {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		Map <String, String> movies = AugGraphLib.id2Name("inputs/moviesTest.txt");
		Map <String, String> actors = AugGraphLib.id2Name("inputs/actorsTest.txt");
		Map <String, List<String>> movie2Actors = AugGraphLib.movie2Actors("inputs/movie-actorsTest.txt", actors, movies);
		Graph<String,String> g = AugGraphLib.buildGraph(actors, movie2Actors);
		
		System.out.println("Commands:\n" + "c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" + 
				"d <low> <high>: list actors sorted by degree, with degree between low and high\n" + 
				"i: list actors with infinite separation from the current center\n" + 
				"p <name>: find path from <name> to current center of the universe\n" + 
				"s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" + 
				"u <name>: make <name> the center of the universe\n" + 
				"q: quit game");
		
		Scanner input = new Scanner(System.in);
		String user_input, command, response;
		response = "Kevin Bacon";
		Graph<String, String> tree = AugGraphLib.bfs(g, response);
		System.out.println(response + " is now the center of the acting universe, connected to " + (g.numVertices() - AugGraphLib.missingVertices(g, tree).size()) + "/" + g.numVertices() + " actors with average separation " + AugGraphLib.averageSeparation(tree, response));
		
		while(true) {
			
			System.out.println(response + " game >");
			
			user_input = input.nextLine();
			
			if(user_input.contains(" ")) {
				command = user_input.substring(0,user_input.indexOf(' '));
				response = user_input.substring(user_input.indexOf(' ') + 1);
			}
			else {
				command = user_input;
			}
			
			tree = AugGraphLib.bfs(g, response);
		
			
			
			
			if(command.equals("c")) {
				int num = Integer.parseInt(response);
				if(num > 0) {
					
				}
				else if(num < 0) {
					
				}
				else {
					
				}
			}
			else if(command.equals("d")) {
				
			}
			else if(command.equals("i")) {
				System.out.println(AugGraphLib.missingVertices(g, tree));
			}
			else if(command.equals("p")) {
				//
				List<String> path = AugGraphLib.getPath(tree, response);
				
				/*
				//System.out.println(tree);
				System.out.println(path);
				System.out.println(path.get(0).toString() + " 's number is " + (path.size()-1));
				//System.out.println(tree.getLabel(path.get(1), path.get(0)));
				System.out.println(g.getLabel(path.get(0), path.get(1)).getClass());
				for (int i = 0; i <path.size()-1; i++) {
				//List<String> comovies = new ArrayList<String>();
					
				
					//System.out.println(g.getLabel(path.get(i+1), path.get(i)));	
			
					System.out.println(tree.getLabel(path.get(i), path.get(i+1)));
					System.out.println(path.get(i) + " appeared in " + tree.getLabel(path.get(i), path.get(i+1)).toString() + " with " + path.get(i+1));
				}
				*/
				System.out.println(response);
			}
			else if(command.equals("s")) {
				List<String> actorsBySep = new ArrayList<String>();
				
			}
			else if(command.equals("u")) {
				if(g.hasVertex(response)) {
					System.out.println(response + " is now the center of the acting universe, connected to " + (g.numVertices() - AugGraphLib.missingVertices(g, tree).size()) + "/" + g.numVertices() + " actors with average separation " + AugGraphLib.averageSeparation(tree, response));
				}
			}
			else if(command.equals("q")) {
				return;
			}
			else {
				System.out.println("Invalid command, try again.");
			}	
		
		}
		
	}

}
