package ru.job4j.hql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import ru.job4j.hql.model.Candidate;

public class HbmRun {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

            Candidate candidate1 = Candidate.of("Alex", 3.5, 275_000);
            Candidate candidate2 = Candidate.of("Pavel", 2.0, 200_000);
            Candidate candidate3 = Candidate.of("John", 0.5, 100_000);

            session.save(candidate1);
            session.save(candidate2);
            session.save(candidate3);

            session.createQuery("from Candidate ")
                    .list()
                    .forEach(System.out::println);

            Candidate alex = (Candidate) session.createQuery("from Candidate c where c.id = :pId")
                    .setParameter("pId", 1)
                    .uniqueResult();
            System.out.println(alex);

            Candidate pavel = (Candidate) session.createQuery("from Candidate c where c.name = :pName")
                    .setParameter("pName", "Pavel")
                    .uniqueResult();
            System.out.println(pavel);

            session.createQuery("update from Candidate c set c.salary = :pSalary where c.id = :pId")
                    .setParameter("pSalary", 120_000)
                    .setParameter("pId", 3)
                    .executeUpdate();

            session.createQuery("delete from Candidate where id = :pId")
                    .setParameter("pId", 1)
                    .executeUpdate();

            session.getTransaction().commit();
            session.close();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}