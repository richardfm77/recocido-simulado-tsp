package mx.unam.heuristicas.model;

public class Connection {
    private int idCity1;
    private int idCity2;
    private double distance;

    public Connection(int idCity1, int idCity2, double distance) {
        this.idCity1 = idCity1;
        this.idCity2 = idCity2;
        this.distance = distance;
    }

    public int getIdCity1() {
        return idCity1;
    }

    public int getIdCity2() {
        return idCity2;
    }

    public double getDistance() {
        return distance;
    }

    public void setIdCity1(int idCity1) {
        this.idCity1 = idCity1;
    }

    public void setIdCity2(int idCity2) {
        this.idCity2 = idCity2;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}