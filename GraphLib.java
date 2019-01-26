import java.io.*;
import java.util.*;

/**
 * Library for graph analysis
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2016
 * @author Yakoob Khan & Aadil Islam
 */

public class GraphLib {
	/**
	 * Takes a random walk from a vertex, up to a given number of steps
	 * So a 0-step path only includes start, while a 1-step path includes start and one of its out-neighbors,
	 * and a 2-step path includes start, an out-neighbor, and one of the out-neighbor's out-neighbors
	 * Stops earlier if no step can be taken (i.e., reach a vertex with no out-edge)
	 * @param g		graph to walk on
	 * @param start	initial vertex (assumed to be in graph)
	 * @param steps	max number of steps
	 * @return		a list of vertices starting with start, each with an edge to the sequentially next in the list;
	 * 			    null if start isn't in graph
	 */
	public static <V,E> List<V> randomWalk(Graph<V,E> g, V start, int steps) {
		// check if start is in the graph and steps > 0
		if (!g.hasVertex(start) || steps < 0) return null;
		
		ArrayList<V> path = new ArrayList<V>();
		V current = start;
		for (int step = 0; step < steps; step++) {
			// if no out edge, stops short and return path
			if (g.outDegree(current) == 0) return path;	
			else {
				// Choose a random index 
				int random = (int)(g.outDegree(current) * Math.random());
				Iterator<V> iter = g.outNeighbors(current).iterator();
				V next = iter.next();
				// Iterate this many times
				while (random > 0) {
					next = iter.next();
					random--;
				}
				path.add(next);		// add the random neighbor to the path
				current = next;	
			}
		}
		return path;
	}
	
	/**
	 * Orders vertices in decreasing order by their in-degree
	 * @param g		graph
	 * @return		list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 */
	public static <V,E> List<V> verticesByInDegree(Graph<V,E> g) {
		ArrayList<V> vertices = new ArrayList<V>();
		// Add all the vertices to the list
		for (V vertex: g.vertices()) vertices.add(vertex);
		// Sort the vertices by descending in-degree
		vertices.sort((V v1, V v2) -> g.inDegree(v2) - g.inDegree(v1));
		return vertices;
	}
	/*
	 * Reads the file and creates a Map from ID to name
	 * @param fileName 	
	 * @return 	Map of ID -> name
	 */
	public static <E> Map<E,E> id2Name(String fileName) throws IOException {
		// Create a new map
		Map<E,E> id2Name = new HashMap<E,E>();
		BufferedReader input= null;
		try {	// Try to read the file
			input = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = input.readLine()) != null) {
				@SuppressWarnings("unchecked")
				// Parse each line into ID and name
				E [] parse = (E[]) line.split("\\|");	
				id2Name.put(parse[0], parse[1]);		// put it in the map
			}
		}
		catch (FileNotFoundException e) {	// Catch the error if file not found
			System.out.println("File not found in directory.");
		}
		finally {	//with a try/catch, you need to close the input no matter what
			if (input!= null) input.close();
		}
		return id2Name;
	}
	/*
	 * Reads the movies to actors file and creates a Map movies -> Set<Actors>
	 * @param fileName
	 * @param id2Actor
	 * @param id2Movie 
	 * @return Map of Movie -> List<Actor>	 	
	 */
	public static <E> Map<E,List<E>> movie2Actors (String fileName, Map<E,E> id2Actor, Map<E,E> id2Movie) throws IOException {
		// Create a new map
		Map<E,List<E>> movie2Actor = new HashMap<E,List<E>>();
		@SuppressWarnings("resource")
		BufferedReader input = null; 
		
		try {	// try to read the file
			input = new BufferedReader(new FileReader(fileName)); 
			String line;
			while ((line = input.readLine()) != null) {
				@SuppressWarnings("unchecked")
				E [] parse = (E[]) line.split("\\|");
				E movie = id2Movie.get(parse[0]);
				E actor = id2Actor.get(parse[1]);
				// Update the map
				if (movie2Actor.containsKey(movie)) {
					movie2Actor.get(movie).add(actor);
				}
				else { 
					movie2Actor.put(movie, new ArrayList<E>());
					movie2Actor.get(movie).add(actor);
				}
			}
		}
		catch (FileNotFoundException e) {	// catch error if file not found
				System.out.println("File not found in directory");
		}
		finally { 	// close file regardless
			if(input != null) input.close(); 
		}
		return movie2Actor;
	}
		
		

	/*
	 * Uses the Map of Movie -> Set<Actor> to build a graph
	 * @param id2Actor map
	 * @param movie2Actors map
	 * @return Graph<V,E> g 	
	 */
	@SuppressWarnings("unchecked")
	public static <V,E> Graph<V, Set<E>> buildGraph(Map<V,E> id2Actor, Map<E,List<E>> movie2Actors) {
		Graph<V,Set<E>> g =  new AdjacencyMapGraph<V,Set<E>>();
		// Add all the actor names as vertices in the graph
		for (V id : id2Actor.keySet()) g.insertVertex((V) id2Actor.get(id));
		// Loop over the all the movies
		for (E movie: movie2Actors.keySet()) {
			ArrayList<E> actors = (ArrayList<E>) movie2Actors.get(movie); 
			// Create edges and edge labels between all the vertices
			for (int i = 0; i< actors.size()-1; i++) {
				for(int j=i+1; j<actors.size(); j++) {
					// If no edge is already present between these two vertices, create new edge and insert movie label
					if (!g.hasEdge((V)actors.get(i), (V)actors.get(j))) {
						g.insertUndirected((V)actors.get(i), (V)actors.get(j), (Set<E>) new HashSet<E>());
						((HashSet<E>) g.getLabel((V)actors.get(i),(V)actors.get(j))).add(movie); 	
					}
					else {	// if edge already present, just add this movie to the label
						((HashSet<E>) g.getLabel((V)actors.get(i),(V)actors.get(j))).add(movie);
					}
				}
			}
		}
		return g;
	}
	/** Breadth First Search
	 * 
	 * @param g -- graph to search
	 * @param source -- starting vertex
	 * @return -- a path tree where the root is the source vertex & the children points to its parent
	 */
	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) {
		//System.out.println("\nBreadth First Search from " + source);
		// Create a new path tree and add the starting vertex as the root of the tree
		Graph<V,E> pathTree = new AdjacencyMapGraph<V,E>();
		pathTree.insertVertex(source);
		
		Set<V> visited = new HashSet<V>(); 		//Set to track which vertices have already been visited
		Queue<V> queue = new LinkedList<V>(); 	//queue to implement BFS
		
		queue.add(source); //enqueue start vertex
		visited.add(source); //add start to visited Set
		while (!queue.isEmpty()) { //loop until no more vertices
			V u = queue.remove(); //dequeue
			for (V v : g.outNeighbors(u)) { //loop over out neighbors
				if (!visited.contains(v)) { //if neighbor not visited, then neighbor is discovered from this vertex
					visited.add(v); //add neighbor to visited Set
					queue.add(v); //enqueue neighbor
					pathTree.insertVertex(v);//create a vertex for this child in the pathTree 
					pathTree.insertDirected(v, u, g.getLabel(v, u)); //create a directed edge from the child to the parent
					pathTree.insertDirected(v, u, (E) new HashSet<E>());
				}
			}
		}
		return pathTree;
	}
	/*
	 * Given a shortest path tree and a vertex, construct a path from the vertex back to the center of the universe
	 */
	public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
		ArrayList<V> path = new ArrayList<V>();
		while (tree.outDegree(v) != 0) {	// Loop back until we hit the center vertex which does not have any out edges
			path.add(v);
			Iterator<V> it = tree.outNeighbors(v).iterator();	
			while(it.hasNext()) v = it.next();	// set v to be the child's parent
		}
		path.add(v);		// add the center of universe to the path
		//System.out.println(path);
		return path;
	}
	/*
	 * Given a graph and a subgraph (here shortest path tree), determine which vertices are in the graph but 
	 * not the subgraph (here, not reached by BFS).
	 */
	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {
		Set<V> SubGraph = new HashSet<V>();
		Set<V> missing = new HashSet<V>();
		// Add all the subgraph vertices to a set
		for (V vertex: subgraph.vertices()) SubGraph.add(vertex);
		for (V vertex: graph.vertices()) {
			// If vertex not found in the sub graph set of vertices, add it to the missing set
			if(!SubGraph.contains(vertex)) missing.add(vertex);
		}
		//System.out.println(missing);
		return missing;

	}
	/*
	 * The average distance-from-root in a shortest path tree
	 */
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
		int totalDistance = averageSeparationHelper(tree, root, 0);
		return (double)totalDistance/(tree.numVertices()-1);
	}
	/*
	 * Helper function: The average distance-from-root in a shortest path tree 
	 */
	private static <V,E> int averageSeparationHelper(Graph<V,E> tree, V root, int total) {
		int sum = total+1;	// the distance at this level of recursion
		// Recurse over each of the child, passing in the current distance
		// and accumulating the total distance in the 'total' instance variable
		for (V child: tree.inNeighbors(root)) {
			total += averageSeparationHelper(tree, child, sum);	
		}
		return total;
	}

	public static void main(String[] args) throws Exception{
		// Test the sample graph provided
		Graph<String, Set<String>> graph = new AdjacencyMapGraph<String, Set<String>>();
		// Insert all the vertices
		graph.insertVertex("Kevin Bacon");
		graph.insertVertex("Bob");
		graph.insertVertex("Alice");
		graph.insertVertex("Charlie");
		graph.insertVertex("Dartmouth (Earl thereof)");
		graph.insertVertex("Nobody");
		graph.insertVertex("Nobody's Friend");
		// Create the edges
		graph.insertUndirected("Kevin Bacon", "Bob", new HashSet<String>());
		graph.getLabel("Kevin Bacon", "Bob").add("A Movie");
		graph.insertUndirected("Kevin Bacon", "Alice", new HashSet<String>());
		graph.getLabel("Kevin Bacon", "Alice").add("A Movie");
		graph.getLabel("Kevin Bacon", "Alice").add("E Movie");
		graph.insertUndirected("Bob", "Alice", new HashSet<String>());
		graph.getLabel("Bob", "Alice").add("A Movie");
		graph.insertUndirected("Alice", "Charlie", new HashSet<String>());
		graph.getLabel("Alice", "Charlie").add("D Movie");
		graph.insertUndirected("Bob", "Charlie", new HashSet<String>());
		graph.getLabel("Bob", "Charlie").add("C Movie");
		graph.insertUndirected("Charlie", "Dartmouth (Earl thereof)", new HashSet<String>());
		graph.getLabel("Charlie", "Dartmouth (Earl thereof)").add("B Movie");
		graph.insertUndirected("Nobody", "Nobody's Friend", new HashSet<String>());
		graph.getLabel("Nobody", "Nobody's Friend").add("F Movie");
		
		System.out.println(graph + "\n"); 
		
		// Create path tree with Kevin Bacon at the center
		Graph<String, Set<String>> tree = bfs(graph, "Kevin Bacon");
		System.out.println(tree + "\n");
		// Get a sample path
		System.out.println(getPath(tree, "Dartmouth (Earl thereof)") + "\n");
		// Get the missing vertices
		System.out.println(missingVertices(graph, tree) + "\n");
		// Get the average separation
		System.out.println(averageSeparation(tree, "Kevin Bacon"));
		
		
		
 	}
}