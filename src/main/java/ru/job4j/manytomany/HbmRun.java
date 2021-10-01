package ru.job4j.manytomany;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.manytomany.model.Address;
import ru.job4j.manytomany.model.Author;
import ru.job4j.manytomany.model.Book;
import ru.job4j.manytomany.model.Person;

public class HbmRun {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

            Address one = Address.of("Kazanskaya", "1");
            Address two = Address.of("Piterskaya", "10");

            Person first = Person.of("Nikolay");
            first.getAddresses().add(one);
            first.getAddresses().add(two);

            Person second = Person.of("Anatoliy");
            second.getAddresses().add(two);

            session.persist(first);
            session.persist(second);

            Person person = session.get(Person.class, 1);
            session.remove(person);

            Book hfJava = Book.of("Head First Java");
            Book hfServlets = Book.of("Head First Servlets & JSP");
            Book hfPatterns = Book.of("Head First Design Patterns");
            Book hfHTML = Book.of("Head First Html With CSS & XHTML");

            Author kathy = Author.of("Kathy Sierra");
            kathy.addBook(hfJava);
            kathy.addBook(hfServlets);
            kathy.addBook(hfPatterns);

            Author bert = Author.of("Bert Bates");
            bert.addBook(hfJava);

            Author elisabeth = Author.of("Elisabeth Freeman");
            elisabeth.addBook(hfPatterns);
            elisabeth.addBook(hfHTML);

            session.persist(kathy);
            session.persist(bert);
            session.persist(elisabeth);

            Author author = session.get(Author.class, bert.getId());
            session.remove(author);

            session.getTransaction().commit();
            session.close();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}