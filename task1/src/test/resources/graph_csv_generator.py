from json import load
import networkx as nx

def generate_graph():
    # generate random directed networkx graph
    G = nx.gnm_random_graph(1000, 100, directed=True)

    # Return a list of edges separated by comma and save them to csv
    def get_edges(G):
        edges = []
        for edge in G.edges():
            edges.append(str(edge[0]) + "," + str(edge[1]))
        return edges

    edges = get_edges(G)
    # save edges to csv
    with open("edges.csv", "w") as f:
        for edge in edges:
            f.write(edge + "\n")

def solve_graph(graph, n):
    paths = []
    no_path = 0
    # iterate through all nodes in graph
    for node1 in graph.nodes():
        for node2 in graph.nodes():
            # if node1 and node2 are not the same node
            if node1 != node2:
                # check if there exists a path of length n between node1 and node2
                if nx.has_path(graph, source=node1, target=node2):
                    if nx.shortest_path_length(graph, node1, node2) == n:
                        print(nx.shortest_path_length(graph, node1, node2))
                        paths.append((node1, node2))
                    else:
                        no_path += 1
                else:
                    no_path += 1
                
    print(f"{no_path} no path")
    print(f"paths: {len(paths)}")
    result = "Set("
    for path in paths:
        result += "(" + str(path[0]) + "," + str(path[1]) + ")"
        if path != paths[-1]:
            result += ","
    result += ")"
    # save result to file
    with open("graph_solution.txt", "w") as f:
        f.write(result)

    return paths

def load_graph():
    # load graph from csv
    G = nx.read_edgelist("edges.csv", delimiter=",", create_using=nx.DiGraph())
    return G

generate_graph()
solve_graph(load_graph(), 2)
