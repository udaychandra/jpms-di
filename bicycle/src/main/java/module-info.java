module ud.jpms.bicycle {
    exports ud.jpms.bicycle;
    exports ud.jpms.bicycle.rent;

    uses ud.jpms.bicycle.Bicycle;
    uses ud.jpms.bicycle.rent.RentalManager;

    provides ud.jpms.bicycle.Bicycle
            with ud.jpms.bicycle.CityBicycle, ud.jpms.bicycle.MountainBicycle;

    provides ud.jpms.bicycle.rent.RentalManager
            with ud.jpms.bicycle.rent.SimpleRentalManager;

    requires ud.jpms.core;
}