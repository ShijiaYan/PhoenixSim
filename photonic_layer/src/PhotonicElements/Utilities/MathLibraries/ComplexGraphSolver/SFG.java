package PhotonicElements.Utilities.MathLibraries.ComplexGraphSolver;

import java.util.*;

import PhotonicElements.Utilities.MathLibraries.Complex;

public class SFG {

	private ArrayList<Edge>[] graph;
	private ArrayList<Path> forwardPaths, individualLoops;
	private boolean[] visited;
	private ArrayList<ArrayList<Integer>> allLoops;
	private Hashtable<String, Boolean> orignal;
	private Complex[] deltaM;
	private ArrayList<String> nodesName;

	@SuppressWarnings("unchecked")
	public SFG(int nodes, ArrayList<String> names) {
		graph = new ArrayList[nodes];
		for (int i = 0; i < nodes; i++) {
			graph[i] = new ArrayList<>();
		}
		visited = new boolean[nodes];
		nodesName = names;
	}

	public Complex[] getDeltas(){
		return deltaM;
	}

	public String printAllLoops() {
		if (allLoops == null) {
			constructLoops();
		}

		int level = 1;
		StringBuilder output = new StringBuilder();
		for (ArrayList<Integer> loop : allLoops) {

			output.append("Level " + level).append(" Untouched Loops:\n");
			if (!loop.isEmpty()) {
				int cnt=1;
				for (int i = 0; i < loop.size(); i += level) {
					output.append("Loop #").append(cnt++).append(": ");
					output.append(individualLoops.get(loop.get(i)).getPath());
					Complex gain = individualLoops.get(loop.get(i)).getGain();
					for (int j = 1; j < level; j++) {
						output.append(" , ").append(individualLoops.get(loop.get(i + j)).getPath());
						gain = gain.times(individualLoops.get(loop.get(i+j)).getGain()) ;
					}
					output.append(" (And its/their total Gain = ").append(gain);
					output.append(")\n");
				}
			}
			level++;
			output.append("====================================\n");
		}
		return output.toString();
	}

	public Complex computeGain(int src, int dest) {

		if (deltaM == null) {
			deltaM = new Complex[forwardPaths.size()+1];
			int path = 0;

			Complex ans = Complex.ZERO ;
			for (Path p : forwardPaths) {
				deltaM[path+1] = computeDelta(path);
				ans = ans.plus(deltaM[path+1].times(p.getGain())) ;
				path++;
			}
			return ans;
		}else{
			Complex ans = Complex.ZERO ;
			int path = 0 ;
			for (Path p : forwardPaths) {
				ans = ans.plus(deltaM[++path].times(p.getGain())) ;
			}
			return ans;
		}
	}

	public Complex computeForwardGain() {
		if (deltaM == null) {
			deltaM = new Complex[forwardPaths.size()+1];
			int path = 0;

			Complex ans = Complex.ZERO ;
			for (Path p : forwardPaths) {
				deltaM[path+1] = computeDelta(path);
				ans = ans.plus(deltaM[path+1].times(p.getGain())) ;
				path++;
			}
			return ans;
		}else{
			Complex ans = Complex.ZERO ;
			int path = 0 ;
			for (Path p : forwardPaths) {
				ans = ans.plus(deltaM[++path].times(p.getGain())) ;
			}
			return ans;
		}
	}

	private Complex computeDelta(int num) {

		int sign = -1;
		Complex delta = Complex.ONE ;

		if(allLoops==null){
			printAllLoops();
		}

		orignal = new Hashtable<>(); // remove list
		String[] remove = forwardPaths.get(num).getPath().split(" ");

		for (String a : remove) {
			orignal.put(a, true);
		}

		int levels = allLoops.size();
		System.out.println("Delta "+(num+1));
		for (int level = 0; level < levels; level++) {

			ArrayList<Integer> cur = allLoops.get(level);
			Complex brackerGain = Complex.ZERO ;
			for (int i = 0; i < cur.size(); i += level + 1) {
				Complex termGain = Complex.ONE ;
				for (int j = 0; j <= level; j++) {
					if (isTouched(individualLoops.get(cur.get(i + j)).getPath()
							.split(" "))) {
						termGain = Complex.ZERO;
						break;
					}else{
						System.out.println("UnTouched : "+individualLoops.get(cur.get(i+j)).getPath());
					}
					termGain = termGain.times(individualLoops.get(cur.get(i + j)).getGain()) ;
				}

				brackerGain = brackerGain.plus(termGain) ;

			}
			delta = delta.plus(brackerGain.times(sign)) ;
			sign *= -1;
		}
		System.out.println("Delta "+(num+1)+" = "+delta);
		return delta;
	}

	public Complex computeDelta() {

		constructLoops();

		int sign = -1;
		Complex delta = Complex.ONE ;
		int levels = allLoops.size();
		for (int level = 0; level < levels; level++) {

			ArrayList<Integer> cur = allLoops.get(level);

			Complex bracketGain = Complex.ZERO ;
			for (int i = 0; i < cur.size(); i += level + 1) {
				Complex termGain = individualLoops.get(cur.get(i)).getGain();
				for (int j = 1; j <= level; j++) {
					termGain = termGain.times(individualLoops.get(cur.get(i + j)).getGain()) ;
				}

				bracketGain = bracketGain.plus(termGain) ;
			}

			delta = delta.plus(bracketGain.times(sign)) ;
			sign *= -1;
		}

		return deltaM[0]=delta;
	}

	private void constructLoops() {
		if (allLoops == null) {
			allLoops = new ArrayList<>();
			// Start adding the first level of n'th non-touching loops
			ArrayList<Integer> individual;
			constructIndividualLoops();
			allLoops.add(individual = new ArrayList<>());
			for (int i = 0; i < individualLoops.size(); i++) {
				individual.add(i);
			}
			combination(1);
		}

	}

	public void addArrow(int from, int to, Complex gain) { // from/to base 1
		graph[--from].add(new Edge(--to, gain));
	}

	public void addArrow(String nodeFrom, String nodeTo, Complex gain){
		int from = nodesName.indexOf(nodeFrom) + 1 ;
		int to = nodesName.indexOf(nodeTo) + 1 ;
		graph[--from].add(new Edge(--to, gain));
	}

	public String printForwardPaths(int src, int dest) {
		if (forwardPaths == null)
			buildForwardPaths(src, dest);
		int i = 1;
		StringBuilder output = new StringBuilder();
		for (Path path : forwardPaths) {
			output.append("Forward Path #").append(i++).append(": ").append(path.getPath());
			output.append(" , Gain = ").append(path.getGain()).append("\n");
		}
		return output.toString();
	}

//	public String printForwardPaths(String nodeSrc, String nodeDest) {
//		int src = nodesName.indexOf(nodeSrc) + 1 ;
//		int dest = nodesName.indexOf(nodeDest) + 1 ;
//		if (forwardPaths == null)
//			buildForwardPaths(src, dest);
//		int i = 1;
//		StringBuilder output = new StringBuilder();
//		for (Path path : forwardPaths) {
//			output.append("Forward Path #").append(i++).append(": ").append(path.getPath());
//			output.append(" AND Gain = ").append(path.getGain()).append("\n");
//		}
//		return output.toString();
//	}

	public String printForwardPaths() {
		int i = 1;
		StringBuilder output = new StringBuilder();
		for (Path path : forwardPaths) {
			output.append("Forward Path #").append(i++).append(": ").append(path.getPath());
			output.append(" , Gain = ").append(path.getGain()).append("\n");
		}
		return output.toString();
	}

	public void buildForwardPaths(int src, int dest) {
		forwardPaths = new ArrayList<>();
		visited[src-1]=true;
		DFS(src - 1, dest - 1, Complex.ONE, nodesName.get(src-1));
		visited[src-1]=false;
		deltaM = null;
	}

	public void buildForwardPaths(String nodeSrc, String nodeDest) {
		int src = nodesName.indexOf(nodeSrc) + 1 ;
		int dest = nodesName.indexOf(nodeDest) + 1 ;
		forwardPaths = new ArrayList<>();
		visited[src-1]=true;
		DFS(src - 1, dest - 1, Complex.ONE, nodesName.get(src-1));
		visited[src-1]=false;
		deltaM = null;
	}

	private void constructIndividualLoops() {
		if (individualLoops == null) {

			ArrayList<Path> temp = forwardPaths;
			int prev;
			forwardPaths = new ArrayList<>();
			for (int src = 0; src < graph.length; src++) {
				visited[src] = true;
				for (Edge node : graph[src]) {

					visited[node.getTo()] = true;

					prev = forwardPaths.size();
					DFS(node.getTo(),
							src,
							node.getGain(),
							nodesName.get(src) + " "
									+ nodesName.get(node.getTo()));

					visited[node.getTo()] = false;

					if (prev < forwardPaths.size()) // new paths were added
						checkRepeated(prev); // remove repeated paths

				}
				visited[src] = false;
			}

			individualLoops = forwardPaths;
			forwardPaths = temp;

		}
	}

	private void checkRepeated(int prev) {
		Path recent, tPath;
		int PREV = prev;
		Stack<Integer> removeList = new Stack<>(); // repeated paths to be
													// removed

		for (; prev < forwardPaths.size(); prev++) {

			String[] recentPath = (recent = forwardPaths.get(prev)).getPath()
					.split(" ");
			orignal = new Hashtable<>();
            for (String s : recentPath) {
                orignal.put(s, true);
            }

			String[] tempStrings;
			for (int i = 0; i < PREV; i++) {

				tempStrings = (tPath = forwardPaths.get(i)).getPath()
						.split(" ");

				if (recent.getGain().equals(tPath.getGain()) && isEqual(tempStrings)) {
					removeList.push(prev);
					break;
				}
			}
		}

		while (!removeList.isEmpty()) {
			forwardPaths.remove((int) removeList.pop());
		}
	}

	private boolean isEqual(String[] b) {

		if (orignal.size() != b.length - 1) // because "b" has the src node
												// twice
			return false;

        for (String s : b) {
            if (!orignal.containsKey(s))
                return false;
        }
		return true;
	}

	private void DFS(int cur, int dest, Complex gain, String path) {
		if (cur == dest) {
			if (!path.equals(Integer.toString(dest + 1)))
				forwardPaths.add(new Path(gain, path));
			return;
		}

		int to;
		for (Edge node : graph[cur]) {
			to = node.getTo();

			if (to == dest || !visited[to]) {
				visited[to] = true;
				DFS(to, dest, gain.times(node.getGain()),
						path + " " + nodesName.get(to));
				visited[to] = false;
			}
		}
	}

	private void combination(int level) { // level is in base "1"
		// starting building the (level) non-touching loops
		// from the (level-1) non-touching loops

		if (level > allLoops.size())
			return;

		ArrayList<Integer> curLevel = allLoops.get(level - 1);
		ArrayList<Integer> zeroLevel = allLoops.get(0);

		int length = allLoops.get(level - 1).size(); // length of the (level)
														// list
		int zeroLenght = zeroLevel.size();
		for (int i = 0; i < length; i += level) {

			for (int k = i + level; k < zeroLenght; k++) {

				boolean touched = false;
				for (int j = 0; j < level; j++) {

					if (isTouched(individualLoops.get(zeroLevel.get(k)),
							individualLoops.get(curLevel.get(i + j)))) {
						touched = true;
						break;
					}

				}
				if (!touched) { // if not touched then add this combination
					ArrayList<Integer> nextLevel = new ArrayList<>();
					if (allLoops.size() == level)
						allLoops.add(nextLevel = new ArrayList<>());
					else
						nextLevel = allLoops.get(level);
					// check if this combination of paths were already taken

					for (int j = 0; j < level; j++) {
						nextLevel.add(zeroLevel.get(i + j));
					}
					nextLevel.add(zeroLevel.get(k));

				}
			}
		}
		combination(level + 1);
	}

	private boolean isTouched(Path a, Path b) {

		String[] aa = a.getPath().split(" ");
		String[] bb = b.getPath().split(" ");

		orignal = new Hashtable<>();
        for (String value : aa) {
            orignal.put(value, true);
        }

        for (String s : bb) {
            if (orignal.containsKey(s))
                return true;
        }
		return false;
	}

	private boolean isTouched(String[] a) {

        for (String s : a) {
            if (orignal.containsKey(s))
                return true;
        }
		return false;
	}
}
