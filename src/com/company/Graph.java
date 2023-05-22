package com.company;

import org.w3c.dom.Node;

import java.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Graph {

    int[][] graph;
    ArrayList<ArrayList<Integer>> graph_list = new ArrayList<>();
    int size;
    float density;
    int edges;

    public void setdensity(int size, int edges){
        this.density= (float)edges/ (size * (size-1)); 
    }
    public float getdensity(){
        return this.density;
    }
    public int getedges(){
        return this.edges;
    }


    public Graph(String path) throws IOException {
        System.out.println(path);
        Path p = Paths.get(path);       
        List<String> lines = Files.readAllLines(p);
        String[] string = lines.get(0)
                .split(" ");
        int[] arr = new int[string.length];
        size = Integer.parseInt(string[0]);
        edges = Integer.parseInt(string[1]);
        int e = Integer.parseInt(string[1]);
        graph = new int[size][size];
        for(int i = 0; i < size; i++) {
            ArrayList<Integer> edges = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                graph[i][j] = Integer.MAX_VALUE;
            }
            graph[i][i] = 0;
            graph_list.add(edges);
        }
        for(int i = 1; i <= e; i++)
        {   string = lines.get(i)
                    .split(" ");
            arr = new int[string.length];
            int src = Integer.parseInt(string[0]);
            int dst = Integer.parseInt(string[1]);
            int wght = Integer.parseInt(string[2]);
            graph_list.get(src).add(dst);
            graph[src][dst] = wght;
        }
        setdensity(size, edges);
    }


    public int getSize()
    {
        return size;
    }

    private int get_min_dist(ArrayList<Integer> cost, boolean[] spt)
    {
        int min = Integer.MAX_VALUE, ind = -1;
        for(int i = 0; i < size; i++)
        {
            if (!spt[i] && cost.get(i) <= min) {
                min = cost.get(i);
                ind = i;
            }
        }
        return ind;
    }
    public void dijkstra(ArrayList<Integer> dist, ArrayList<Integer> parents, int src)
    {
        boolean[] shortest_path_tree_set = new boolean[getSize()];
        for(int i = 0; i < getSize(); i++) {
            dist.set(i, Integer.MAX_VALUE);
            shortest_path_tree_set[i] = false;//Shortest path from
            //Src to i is not finalized
        }
        dist.set(src, 0);
        for(int i = 0; i < size-1; i++)
        {
            int u = get_min_dist(dist, shortest_path_tree_set);
            shortest_path_tree_set[u] = true; // Only enters once
            for(int v : graph_list.get(u))
            {
                if(graph[u][v] != Integer.MAX_VALUE && !shortest_path_tree_set[v]
                && dist.get(u) != Integer.MAX_VALUE && dist.get(u) + graph[u][v] < dist.get(v)) {
                    dist.set(v, dist.get(u) + graph[u][v]);
                    parents.set(v, u); // Update parent
                }
            }
        }
    }
    public boolean bellman_ford(ArrayList<Integer> cost, ArrayList<Integer> parents, int src)
    {
        int[] first = new int[size];
        int[] second = new int[size];
        boolean nCycles = false;
        for (int i=0; i<size; i++) {
            second[i] = Integer.MAX_VALUE;
        }
        parents.set(src, src);
        second[src] = 0;
        for (int i=0; i<size+1; i++) {
            first = second.clone();
            for (int j=0; j<size; j++) {
                for (int k=0; k<size; k++) {
                    if (first[k] != Integer.MAX_VALUE && graph[k][j] != Integer.MAX_VALUE && first[j] > first[k] + graph[k][j]) {
                        second[j] = first[k] + graph[k][j];
                        parents.set(j, k);
                    } else second[j] = Math.min(first[j], second[j]);
                }
            }
        }

        for (int i=0; i<size; i++) {
            if (first[i] != second[i]) nCycles = true;
            cost.set(i, first[i]);
        }
        return !nCycles;
    }

    
    public boolean floyd_warshall(ArrayList<ArrayList<Integer>> costs, ArrayList<ArrayList<Integer>> predecessors)
    {
        int i, j, k;
        int [][] dist = new int[getSize()][getSize()];
        boolean nCycles = false;



        for (i=0;i<getSize();i++){
            predecessors.add(new ArrayList<>());
            for (j=0;j< getSize();j++){
                dist[i][j] = graph[i][j];
                predecessors.get(i).add(j, i);  // set all parent

            }
        }






        for (k = 0; k <getSize(); k++) {
            for (i = 0; i < getSize(); i++) {
                for (j = 0; j < getSize(); j++) {
                    if (dist[k][j] != Integer.MAX_VALUE && dist[i][k] != Integer.MAX_VALUE && dist[i][k] + dist[k][j]< dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        predecessors.get(i).set(j, predecessors.get(k).get(j));
                    }
                }
            }
        }


        for (i=0; i<dist.length; i++) {
            ArrayList<Integer> rowList = new ArrayList<>();
            for (j=0; j<dist[i].length; j++) {
                rowList.add(dist[i][j]);
            }
            if (dist[i][i] < 0) nCycles = true;
            costs.add(rowList);
        }
        return !nCycles;
    }
}
