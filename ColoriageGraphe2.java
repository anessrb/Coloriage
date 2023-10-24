import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

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
    int x; // Coordonnée x
    int y; // Coordonnée y

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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class GraphVisualization extends JPanel {
    private Graphe graphe;

    public GraphVisualization(Graphe graphe) {
        this.graphe = graphe;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int radius = 20; // Rayon des cercles représentant les sommets

        for (Sommet sommet : graphe.vertices) {
            int x = sommet.getX();
            int y = sommet.getY();

            // Dessinez un cercle colorié pour représenter le sommet
            g2d.setColor(getColor(sommet.getColor()));
            g2d.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        }

        // Dessinez les arêtes entre les sommets ici
        for (Pair<Sommet, Sommet> edge : graphe.edges) {
            int x1 = edge.left.getX();
            int y1 = edge.left.getY();
            int x2 = edge.right.getX();
            int y2 = edge.right.getY();
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    private Color getColor(int color) {
        switch (color) {
            case 1:
                return Color.RED;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.BLUE;
            default:
                return Color.BLACK;
        }
    }
}

class Graphe {
    ArrayList<Sommet> vertices;
    ArrayList<Pair<Sommet, Sommet> > inter;
    ArrayList<Pair<Sommet, Sommet> > pref;
    ArrayList<Pair<Sommet, Sommet> > edges = new ArrayList<>();
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
        for (Pair<Sommet, Sommet> p : pref) {
            this.edges.add(p);
        }
        for (Pair<Sommet, Sommet> i : inter) {
            this.edges.add(i);
        }
    }

    void initDegree(ArrayList<Pair<Sommet, Sommet>> edges, ArrayList<Sommet> vertices) {
        for (Sommet s : vertices) {
            for (Pair<Sommet, Sommet> edge : edges) {
                if (edge.left.name.equals(s.name) || edge.right.name.equals(s.name)) {
                    s.setDegree(s.getDegree() + 1);
                }
            }
        }
    }

    ArrayList<Sommet> removeSommet(ArrayList<Sommet> vertices, Sommet sommet) {
        if (vertices.contains(sommet)) {
            vertices.remove(vertices.indexOf(sommet));
        }
        return vertices;
    }

    ArrayList<Pair<Sommet, Sommet>> removeEdges(ArrayList<Pair<Sommet, Sommet>> edges, Sommet sommet) {
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
        int max = vertices.get(0).getDegree();
        for (Sommet s : vertices) {
            if (max < s.getDegree()) {
                max = s.getDegree();
            }
        }
        return vertices.get(max);
    }

    ArrayList<Sommet> asPref(Sommet sommet) {
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
        for (int i = 1; i <= k; i++) {
            if (!colors.contains(i)) {
                return i;
            }
        }
        return -1;
    }

    public void visualizeGraph() {
        JFrame frame = new JFrame("Visualisation du graphe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new GraphVisualization(this));
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    void initColor() {
        ArrayList<Integer> usedColors = new ArrayList<>();
        int maxRadius = 200;
        int centerX = 400;
        int centerY = 300;
        double angleIncrement = 2 * Math.PI / vertices.size();
        int currentAngle = 0;

        for (Sommet sommet : vertices) {
            ArrayList<Sommet> neighbors = getNeighbours(sommet);
            ArrayList<Integer> neighborColors = new ArrayList<>();
            for (Sommet neighbor : neighbors) {
                if (neighbor.getColor() > 0) {
                    neighborColors.add(neighbor.getColor());
                }
            }

            int color = 1;
            while (true) {
                if (!neighborColors.contains(color)) {
                    sommet.setColor(color);
                    break;
                }
                color++;
            }

            if (!usedColors.contains(color)) {
                usedColors.add(color);
            }

            int x = (int) (centerX + maxRadius * Math.cos(currentAngle));
            int y = (int) (centerY + maxRadius * Math.sin(currentAngle));
            sommet.setCoordinates(x, y);

            currentAngle += angleIncrement;
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
        Sommet a = new Sommet("a");
        Sommet b = new Sommet("b");
        Sommet c = new Sommet("c");
        Sommet d = new Sommet("d");

        ArrayList<Sommet> vertices1 = new ArrayList<Sommet>();
        vertices1.add(a);
        vertices1.add(b);
        vertices1.add(c);
        vertices1.add(d);

        ArrayList<Pair<Sommet, Sommet>> inter1 = new ArrayList<Pair<Sommet, Sommet>>();
        inter1.add(new Pair(a, b));
        inter1.add(new Pair(b, c));
        inter1.add(new Pair(c, d));
        inter1.add(new Pair(d, a));

        ArrayList<Pair<Sommet, Sommet>> pref1 = new ArrayList<Pair<Sommet, Sommet>>();

        Graphe g1 = new Graphe(vertices1, inter1, pref1, Integer.parseInt(args[0]));

        System.out.println("--- Graphe #1 ---");
        g1.initColor();
        g1.printColor();

        // ----- GRAPHE 2 ----- //
        Sommet u = new Sommet("u");
        Sommet v = new Sommet("v");
        Sommet t = new Sommet("t");
        Sommet x = new Sommet("x");
        Sommet y = new Sommet("y");
        Sommet z = new Sommet("z");

        ArrayList<Sommet> vertices2 = new ArrayList<Sommet>();
        vertices2.add(u);
        vertices2.add(v);
        vertices2.add(t);
        vertices2.add(x);
        vertices2.add(y);
        vertices2.add(z);

        ArrayList<Pair<Sommet, Sommet>> inter2 = new ArrayList<Pair<Sommet, Sommet>>();
        inter2.add(new Pair(u, x));
        inter2.add(new Pair(u, y));
        inter2.add(new Pair(v, x));
        inter2.add(new Pair(v, z));
        inter2.add(new Pair(v, t));
        inter2.add(new Pair(y, x));
        inter2.add(new Pair(y, t));

        ArrayList<Pair<Sommet, Sommet>> pref2 = new ArrayList<Pair<Sommet, Sommet>>();
        pref2.add(new Pair(u, t));

        Graphe g2 = new Graphe(vertices2, inter2, pref2, Integer.parseInt(args[0]));

        System.out.println("\n--- Graphe #2 ---");
        g2.initColor();
        g2.printColor();

        g1.visualizeGraph();
        g2.visualizeGraph();
    }
}
