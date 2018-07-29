package ud.jpms.bicycle;

import ud.jpms.bicycle.rent.RentalManager;
import ud.jpms.core.IOC;

import java.util.Optional;

public class Main {

    public static void main(String... args) {

        RentalManager manager = IOC.newService(RentalManager.class);

        manager.rent(1);

        ModuleLayer layer = Main.class.getModule().getLayer();

        Optional<Module> module = layer.findModule("ud.jpms.bicycle");

        module.ifPresent(m -> {
            var clazz = Class.forName(module.get(), "ud.jpms.bicycle.Bicycle");

            System.out.println(clazz.getName());

        });
    }
}
