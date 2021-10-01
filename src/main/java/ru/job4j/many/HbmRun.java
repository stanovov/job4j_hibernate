package ru.job4j.many;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.many.model.Brand;
import ru.job4j.many.model.Model;
import ru.job4j.many.model.Role;
import ru.job4j.many.model.User;

import java.util.List;

public class HbmRun {

    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

            User one = User.of("Petr");
            session.save(one);

            Role admin = Role.of("ADMIN");
            admin.addUser(session.load(User.class, 1));
            session.save(admin);

            Brand toyota = Brand.of("Toyota");
            toyota.addModel(Model.of("Sprinter Trueno"));
            toyota.addModel(Model.of("Supra"));
            toyota.addModel(Model.of("Mark ||"));
            toyota.addModel(Model.of("Chaser"));
            toyota.addModel(Model.of("GT86"));
            session.save(toyota);

            session.getTransaction().commit();
            session.close();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}