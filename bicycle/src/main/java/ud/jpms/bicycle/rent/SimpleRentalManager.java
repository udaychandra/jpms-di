package ud.jpms.bicycle.rent;

import ud.jpms.bicycle.Bicycle;
import ud.jpms.core.Cardinality;
import ud.jpms.core.Reference;

import java.util.List;

public class SimpleRentalManager implements RentalManager {

    private List<Bicycle> bicycles;

    @Reference(cardinality = Cardinality.ONE_OR_MORE)
    public void setBicycles(List<Bicycle> bicycles) {
        this.bicycles = bicycles;
    }

    @Override
    public void rent(int quantity) {
        System.out.println(String.format("%d available, %d requested", this.bicycles.size(), quantity));
    }
}

