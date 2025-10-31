package map_to_kml.trips.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Trips implements Iterable<Trip> {

    private final List<Trip> _trips = new LinkedList<>();

    public Trips() {
        // ...
    }

    public void addTrip(Trip trip) {
        _trips.add(trip);
    }

    public List<Trip> getTrips() {
        return _trips;
    }

    @Override
    public Iterator<Trip> iterator() {
        return _trips.iterator();
    }

    public void clear() {
        _trips.clear();
    }

    public int size() {
        return _trips.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Trips [_trips=");
        builder.append(_trips);
        builder.append("]");
        return builder.toString();
    }

}
