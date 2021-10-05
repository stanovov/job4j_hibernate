package ru.job4j.hql.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "vacancies_base")
public class VacanciesBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            joinColumns = {@JoinColumn(name = "vacancies_base_id")},
            inverseJoinColumns = {@JoinColumn(name = "vacancy_id")}
    )
    private List<Vacancy> vacancies = new ArrayList<>();

    public void addVacancy(Vacancy vacancy) {
        this.vacancies.add(vacancy);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Vacancy> getVacancies() {
        return vacancies;
    }

    public void setVacancies(List<Vacancy> vacancies) {
        this.vacancies = vacancies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VacanciesBase that = (VacanciesBase) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "VacanciesBase{"
                + "id=" + id
                + ", vacancies=" + vacancies
                + '}';
    }
}
