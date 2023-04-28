import java.util.*;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.*;


public class CallGraphListener extends Java8BaseListener{
    static class Graph {
        HashSet<String> nodes = new HashSet<String>();
        MultiMap<String, String> edges = new MultiMap<String, String>();
        
        public void edge(String source, String target) {
            if(!(edges.containsKey(source) && edges.get(source).contains(target))){
                edges.put(source, target);  
            }
        }
        
        public String doting() { // create the graph.
            StringBuilder buffer = new StringBuilder();
            buffer.append("    ranksep=.50;\n"); 
            buffer.append("    edge [arrowsize=.8]\n");
            buffer.append("    node [shape=circle, fontname=\"Calibri\",\n");
            buffer.append("            fontsize=9, fixedsize=true, height=1, style = filled];\n");
            

            List<String> sorted = sort(edges.keySet());
            List<String> sortedEdges = new ArrayList<String>();

            for (String src : sorted) {
                sortedEdges.addAll(edges.get(src)); // add all edges to sortedEdges.
            }

            sortedEdges = sortedEdges.stream().sorted().collect(Collectors.toList()); 

            for (String edge : sortedEdges){ 
                String name = createLabelString(edge); // get the name of the edge in proper form.
                if(buffer.indexOf(name) == -1){
                    buffer.append("    " + name + " [label = \"" + edge + "\""); // add the edge to the graph.   
                    for (String src : nodes) {
                        if(src.contains(name.substring(0,1)) && src.contains(name.substring(1))){ // if the node is in the source, color it green.
                            buffer.append(",color=green");
                        }
                    }
                    buffer.append("];\n");
                }
            }
            for (String node : nodes){ // add the not connected nodes to the graph.
                String name = createLabelString(node); // get the name of the node in proper form.) 
                if(buffer.indexOf(name) == -1){
                    buffer.append("    " + name + " [label = \"" + node + "\""); 
                    buffer.append(",color=green");
                    buffer.append("];\n");
                }
            }
            for (String src : sorted){ //add the edges to the graph.
                String name = createLabelString(src); 
                for (String tar : edges.get(src)) {
                    String target = createLabelString(tar);
                    buffer.append("    " + name + " -> " + target + ";\n"); 
                }
            }
            return buffer.toString();
        }


        @Override
        public String toString() {
            return "Graph{" + "nodes=" + nodes + ", edges=" + edges +'}';
        }

        public String createLabelString(String name){
            String[] label = name.split("/"); // com.acme/A/m1
            return label[label.length-2]+label[label.length-1]; // Am1
        }
    }

    public static List<String> sort(Set<String> set){
        return set.stream().sorted().collect(Collectors.toList());
    }

    Graph graph = new Graph();
    String currentFunctionName = null;
    String packageName = null;
    String className = null;

    @Override
    public void enterA(Java8Parser.AContext ctx) { // get the function name.
        //ctx.methodName().Identifier() returns the name of the method which is called.
        String functionName = String.valueOf(packageName + "/" +className+"/" +ctx.methodName().Identifier()); 
        graph.edge(packageName + "/" +className+"/" +currentFunctionName, functionName);
    }

    @Override
    public void enterB(Java8Parser.BContext ctx){ // get the function name.
        String funcName = String.valueOf(packageName +"/"+ ctx.typeName().Identifier() + "/" + ctx.Identifier());
        graph.edge(packageName + "/" +className+"/" +currentFunctionName, funcName);
    }

    @Override
    public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx){ // get the function name.
        currentFunctionName = String.valueOf(ctx.methodHeader().methodDeclarator().Identifier());
        graph.nodes.add(packageName + "/" +className+"/" +currentFunctionName);
    }


    @Override
    public void enterClassDeclaration(Java8Parser.ClassDeclarationContext ctx){ // get the class name.
        className = String.valueOf(ctx.normalClassDeclaration().Identifier());
    }

    @Override
    public void enterPackageDeclaration(Java8Parser.PackageDeclarationContext ctx){ // get the package name.
        StringBuilder name = new StringBuilder();
        for (int i = 0; i <ctx.Identifier().size(); i++) {
            if(i != 0 ){
                name.append(".");
            }
            name.append(String.valueOf(ctx.Identifier().get(i)));
        }
        packageName = String.valueOf(name);
    }

    public static void main(String[] args) throws Exception {
        ANTLRInputStream input = new ANTLRInputStream(System.in);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        CallGraphListener listener = new CallGraphListener();
        // This is where we trigger the walk of the tree using our listener.
        walker.walk(listener, tree);

        StringBuilder buf = new StringBuilder();
        buf.append("digraph G {\n");
        buf.append(listener.graph.doting());
        buf.append("}");
        System.out.println(buf.toString());

    }

}
class MultiMap<K, V>
{
    private Map<K, Collection<V>> map = new HashMap<>();

    public void put(K key, V value) {
        if (map.get(key) == null){
            map.put(key, new ArrayList<V>());
        }
        map.get(key).add(value);
    }

    public void putIfAbsent(K key, V value){
        if (map.get(key) == null){
            map.put(key, new ArrayList<>());
        }
        if (!map.get(key).contains(value)){
            map.get(key).add(value);
        }
    }

    public Collection<V> get(Object key){
        return map.get(key);
    }

    public Set<K> keySet(){
        return map.keySet();
    }

    public Set<Map.Entry<K, Collection<V>>> entrySet(){
        return map.entrySet();
    }

    public Collection<Collection<V>> values(){
        return map.values();
    }

    public boolean containsKey(Object key){
        return map.containsKey(key);
    }

    public Collection<V> remove(Object key){
        return map.remove(key);
    }

    public int size(){
        int size = 0;
        for (Collection<V> value: map.values()) {
            size += value.size();
        }
        return size;
    }

    public boolean isEmpty(){
        return map.isEmpty();
    }

    public void clear(){
        map.clear();
    }

    public boolean remove(K key, V value){
        if (map.get(key) != null){
            return map.get(key).remove(value);
        }
        return false;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (map.get(key) != null) {
            if (map.get(key).remove(oldValue)){
                return map.get(key).add(newValue);

            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "MultiMap{" + "map=" + map + '}';
    }
}