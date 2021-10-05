package ru.job4j.hql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import ru.job4j.hql.model.Candidate;
import ru.job4j.hql.model.VacanciesBase;
import ru.job4j.hql.model.Vacancy;

public class HbmRun {
    public static void main(String[] args) {
        Candidate rsl = null;
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

            Vacancy vacancy1 = Vacancy.of("Java Developer", "Accenture");
            Vacancy vacancy2 = Vacancy.of("Java Developer", "EPAM");
            Vacancy vacancy3 = Vacancy.of("Junior Java Developer", "Sber");

            VacanciesBase vacanciesBase = new VacanciesBase();
            vacanciesBase.addVacancy(vacancy1);
            vacanciesBase.addVacancy(vacancy2);
            vacanciesBase.addVacancy(vacancy3);
            session.save(vacanciesBase);

            Candidate candidate = Candidate.of("Semyon", 0.5, 100_000);
            candidate.setVacanciesBase(vacanciesBase);
            session.save(candidate);

            rsl = session.createQuery("select distinct c from Candidate c "
                    + "join fetch c.vacanciesBase vb "
                    + "join fetch vb.vacancies v "
                    + "where c.id = :pID", Candidate.class
            ).setParameter("pID", 1).uniqueResult();

            session.getTransaction().commit();
            session.close();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        System.out.println(rsl);
    }
}