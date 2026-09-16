package mx.unam.heuristicas.model;

public class City {

    private final int id;
    private final String name;
    private final String country;
    private final int population;
    private final double latitude;
    private final double longitude;

    public City(
            int id,
            String name,
            String country,
            int population,
            double latitude,
            double longitude
    ) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.population = population;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public int getPopulation() {
        return population;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}