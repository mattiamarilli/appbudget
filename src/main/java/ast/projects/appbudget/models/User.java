package ast.projects.appbudget.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name= "name", nullable = false, unique = true)
    private String name;
    
    @Column(name = "surname", nullable = false, unique = true)
    private String surname;

    public User() {}

    public User(String name, String surname) {
        //validateName(name);
        //validateSurname(surname);
        this.name = name;
        this.surname = surname;
    }
    
    public User(long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        //validateName(name);
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        //validateSurname(surname);
        this.surname = surname;
    }

	/*
	 * private void validateName(String name) { if (name == null) { throw new
	 * IllegalArgumentException("Name or surname cannot be null"); } if
	 * (name.trim().isEmpty()) { throw new
	 * IllegalArgumentException("Name or surname cannot be empty"); } }
	 * 
	 * private void validateSurname(String surname) { if (surname == null) { throw
	 * new IllegalArgumentException("Name or surname cannot be null"); } if
	 * (surname.trim().isEmpty()) { throw new
	 * IllegalArgumentException("Name or surname cannot be empty"); } }
	 */
    
    @Override
    public String toString() {
        return name + " " + surname;
    }
}
