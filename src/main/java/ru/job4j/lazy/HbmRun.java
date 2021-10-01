package ru.job4j.lazy;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.lazy.model.Brand;
import ru.job4j.lazy.model.Category;
import ru.job4j.lazy.model.Model;
import ru.job4j.lazy.model.Task;

import java.util.ArrayList;
import java.util.List;

public class HbmRun {
    public static void main(String[] args) {
        List<Category> list = new ArrayList<>();
        List<Brand> brands = new ArrayList<>();
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

            Category category = Category.of("Consulting");
            Task task1 = Task.of("Consultation on Hibernate", category);
            Task task2 = Task.of("Consultation on Spring", category);
            Task task3 = Task.of("Consultation on Servlet", category);
            category.getTasks().add(task1);
            category.getTasks().add(task2);
            category.getTasks().add(task3);
            session.save(category);
            session.save(task1);
            session.save(task2);
            session.save(task3);

            list = session.createQuery(
                    "select distinct c from Category c join fetch c.tasks"
            ).list();

            Brand lamborghini = Brand.of("Lamborghini");
            Model gallardo = Model.of("Gallardo", lamborghini);
            Model huracan = Model.of("Huracan", lamborghini);
            Model murcielago = Model.of("Murcielago", lamborghini);
            Model diablo = Model.of("Diablo", lamborghini);
            Model aventador = Model.of("Aventador", lamborghini);
            lamborghini.addModel(gallardo);
            lamborghini.addModel(huracan);
            lamborghini.addModel(murcielago);
            lamborghini.addModel(diablo);
            lamborghini.addModel(aventador);
            session.save(lamborghini);
            session.save(gallardo);
            session.save(huracan);
            session.save(murcielago);
            session.save(diablo);
            session.save(aventador);

            brands = session.createQuery(
                    "select distinct b from Brand b join fetch b.models"
            ).list();

            session.getTransaction().commit();
            session.close();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        for (Task task : list.get(0).getTasks()) {
            System.out.println(task);
        }
        for (Model model : brands.get(0).getModels()) {
            System.out.println(model);
        }
    }
}
