import java.util.ArrayList;

class Pair<L, R> {
    final L left;
    final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }
}

class Sommet {
    String name;
    int color;
    int degree;

    public Sommet(String name) {
        this.name = name;
        this.color = 0;
        this.degree = 0;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public int getDegree() {
        return degree;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
}

class Graphe {
    ArrayList<Sommet> vertices;
    ArrayList<Pair<Sommet, Sommet>> inter;
    ArrayList<Pair<Sommet, Sommet>> pref;
    ArrayList<Pair<Sommet, Sommet>> edges = new ArrayList<>();
    int k;

    public Graphe(ArrayList<Sommet> vertices, ArrayList<Pair<Sommet, Sommet>> inter, ArrayList<Pair<Sommet, Sommet>> pref, int k) {
        this.vertices = vertices;
        this.inter = inter;
        this.pref = pref;
        this.k = k;
        this.initEdges();
        this.initDegree(this.inter, this.vertices);
    }

    void initEdges() {
        // Initialise un tableau avec TOUTES les arêtes
        for (Pair<Sommet, Sommet> p : pref) {
            this.edges.add(p);
        }
        for (Pair<Sommet, Sommet> i : inter) {
            this.edges.add(i);
        }
    }

    void initDegree(ArrayList<Pair<Sommet, Sommet>> edges, ArrayList<Sommet> vertices) {
        // Initialise les degrés de chaque sommet
        for (Sommet s : vertices) {
            for (Pair<Sommet, Sommet> edge : edges) {
                if (edge.left.name.equals(s.name) || edge.right.name.equals(s.name)) {
                    s.setDegree(s.getDegree() + 1);
                }
            }
        }
    }

    ArrayList<Sommet> removeSommet(ArrayList<Sommet> vertices, Sommet sommet) {
        // Renvoie une liste de sommets privée du sommet donné en paramètre
        if (vertices.contains(sommet)) {
            vertices.remove(vertices.indexOf(sommet));
        }
        return vertices;
    }

    ArrayList<Pair<Sommet, Sommet>> removeEdges(ArrayList<Pair<Sommet, Sommet>> edges, Sommet sommet) {
        // Renvoie une liste d'arêtes privée de celles étant reliées au sommet donné en paramètre
        ArrayList<Pair<Sommet, Sommet>> result = new ArrayList<>();
        for (Pair<Sommet, Sommet> edge : edges) {
            if (!edge.left.name.equals(sommet.name) && !edge.right.name.equals(sommet.name)) {
                result.add(edge);
            } else if (!edge.left.name.equals(sommet.name) && edge.right.name.equals(sommet.name)) {
                edge.left.setDegree(edge.left.getDegree() - 1);
            } else {
                edge.right.setDegree(edge.right.getDegree() - 1);
            }
        }
        return result;
    }

    Sommet getSommetDegreeMin(ArrayList<Sommet> vertices) {
        // Retourne le sommet avec le degré le plus faible et < k
        int min = k;
        Sommet result = vertices.get(0);
        for (Sommet s : vertices) {
            if (min >= s.getDegree() && s.getDegree() < this.k) {
                min = s.getDegree();
                result = s;
            }
        }
        if (min == k) {
            return null;
        } else {
            return result;
        }
    }

    Sommet getSommetDegreeMax(ArrayList<Sommet> vertices) {
        // Retourne le sommet avec le degré le plus élevé
        int max = vertices.get(0).getDegree();
        for (Sommet s : vertices) {
            if (max < s.getDegree()) {
                max = s.getDegree();
            }
        }
        return vertices.get(max);
    }

    ArrayList<Sommet> asPref(Sommet sommet) {
        // Si le sommet donné en paramètre fait partie des sommets de préférences,
        // renvoie tous les sommets, eux aussi de préférence, avec lesquels il est lié
        ArrayList<Sommet> res = new ArrayList<>();
        for (Pair<Sommet, Sommet> p : this.pref) {
            if (p.left.name.equals(sommet.name)) {
                res.add(p.right);
            }
            if (p.right.name.equals(sommet.name)) {
                res.add(p.left);
            }
        }
        return res;
    }

    ArrayList<Sommet> getNeighbours(Sommet sommet) {
        // Retourne les sommets avec lesquels le sommet en paramètre est lié
        ArrayList<Sommet> result = new ArrayList<>();
        for (Pair<Sommet, Sommet> p : this.edges) {
            if (sommet.equals(p.left)) {
                result.add(p.right);
            }
            if (sommet.equals(p.right)) {
                result.add(p.left);
            }
        }
        return result;
    }

    int getNewColor(ArrayList<Integer> colors) {
        // Permet de retourner une nouvelle couleur en fonction des couleurs déjà vues
        for (int i = 1; i <= k; i++) {
            if (!colors.contains(i)) {
                return i;
            }
        }
        return -1;
    }

    

    void initColor() {
        // Créez un tableau pour suivre les couleurs déjà attribuées.
        ArrayList<Integer> usedColors = new ArrayList<>();

        // Parcourez les sommets et attribuez des couleurs en respectant les préférences.
        for (Sommet sommet : vertices) {
            // Obtenez les voisins du sommet.
            ArrayList<Sommet> neighbors = getNeighbours(sommet);

            // Créez une liste des couleurs déjà utilisées par les voisins.
            ArrayList<Integer> neighborColors = new ArrayList<>();
            for (Sommet neighbor : neighbors) {
                if (neighbor.getColor() > 0) {
                    neighborColors.add(neighbor.getColor());
                }
            }

            // Parcourez les couleurs possibles.
            int color = 1;
            while (true) {
                // Si la couleur n'est pas utilisée par les voisins, attribuez-la au sommet.
                if (!neighborColors.contains(color)) {
                    sommet.setColor(color);
                    break;
                }
                color++;
            }

            // Mettez à jour la liste des couleurs déjà attribuées.
            if (!usedColors.contains(color)) {
                usedColors.add(color);
            }
        }
    }

    void printDegree(ArrayList<Sommet> vertices) {
        System.out.print("deg -> ");
        for (Sommet s : vertices) {
            System.out.print(s.getName() + ":" + s.getDegree() + " ");
        }
        System.out.println("");
    }

    void printEdges(ArrayList<Pair<Sommet, Sommet>> edges) {
        System.out.print("Edges -> ");
        for (Pair<Sommet, Sommet> p : edges) {
            System.out.print("(" + p.left.name + "," + p.right.name + ") ");
        }
        System.out.println("");
    }

    void printColor() {
        System.out.print("color -> ");
        for (Sommet s : this.vertices) {
            System.out.print(s.getName() + ":" + s.getColor() + " ");
        }
        System.out.println("");
    }
}

public class ColoriageGraphe {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Utilisation : java ColoriageGraphe nombre_couleurs");
            System.exit(0);
        }

        // ----- Graphe 1 ----- //
        // Création des sommets
        Sommet a = new Sommet("a");
        Sommet b = new Sommet("b");
        Sommet c = new Sommet("c");
        Sommet d = new Sommet("d");

        // Ajout des sommets dans un tableau
        ArrayList<Sommet> vertices1 = new ArrayList<Sommet>();
        vertices1.add(a);
        vertices1.add(b);
        vertices1.add(c);
        vertices1.add(d);

        // Création des arêtes d'interférences
        ArrayList<Pair<Sommet, Sommet>> inter1 = new ArrayList<Pair<Sommet, Sommet>>();
        inter1.add(new Pair(a, b));
        inter1.add(new Pair(b, c));
        inter1.add(new Pair(c, d));
        inter1.add(new Pair(d, a));

        // Création des arêtes de préférences
        ArrayList<Pair<Sommet, Sommet>> pref1 = new ArrayList<Pair<Sommet, Sommet>>();

        // Création du graphe
        Graphe g1 = new Graphe(vertices1, inter1, pref1, Integer.parseInt(args[0]));

        System.out.println("--- Graphe #1 ---");
        // Colorisation du graphe
        g1.initColor();

        // Affichage des couleurs
        g1.printColor();

        // ----- GRAPHE 2 ----- //
        // Création des sommets
        Sommet u = new Sommet("u");
        Sommet v = new Sommet("v");
        Sommet t = new Sommet("t");
        Sommet x = new Sommet("x");
        Sommet y = new Sommet("y");
        Sommet z = new Sommet("z");

        // Ajout des sommets dans un tableau
        ArrayList<Sommet> vertices2 = new ArrayList<Sommet>();
        vertices2.add(u);
        vertices2.add(v);
        vertices2.add(t);
        vertices2.add(x);
        vertices2.add(y);
        vertices2.add(z);

        // Création des arêtes d'interférences
        ArrayList<Pair<Sommet, Sommet>> inter2 = new ArrayList<Pair<Sommet, Sommet>>();
        inter2.add(new Pair(u, x));
        inter2.add(new Pair(u, y));
        inter2.add(new Pair(v, x));
        inter2.add(new Pair(v, z));
        inter2.add(new Pair(v, t));
        inter2.add(new Pair(y, x));
        inter2.add(new Pair(y, t));

        // Création des arêtes de préférences
        ArrayList<Pair<Sommet, Sommet>> pref2 = new ArrayList<Pair<Sommet, Sommet>>();
        pref2.add(new Pair(u, t));

        // Création du graphe
        Graphe g2 = new Graphe(vertices2, inter2, pref2, Integer.parseInt(args[0]));

        System.out.println("\n--- Graphe #2 ---");
        // Colorisation du graphe
        g2.initColor();

        // Affichage des couleurs
        g2.printColor();
    }
}
