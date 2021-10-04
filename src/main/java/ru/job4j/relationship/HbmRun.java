package ru.job4j.relationship;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.relationship.model.Car;
import ru.job4j.relationship.model.Driver;
import ru.job4j.relationship.model.Engine;

public class HbmRun {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

            Engine engine = Engine.of("V8 FSI 4.2", "A2312133 U23239239");
            session.save(engine);

            Driver driver1 = Driver.of("Alex");
            session.save(driver1);
            Driver driver2 = Driver.of("Max");
            session.save(driver2);

            Car car = Car.of("Audi S5", engine);
            car.addDriver(driver1);
            car.addDriver(driver2);
            session.save(car);

            Car audi = session.get(Car.class, car.getId());
            for (Driver driver : audi.getDrivers()) {
                System.out.println(driver.getName());
            }

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
