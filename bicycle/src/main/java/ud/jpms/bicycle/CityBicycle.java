package ud.jpms.bicycle;

public class CityBicycle implements Bicycle {

    @Override
    public String name() {
        return CityBicycle.class.getName();
    }
}

