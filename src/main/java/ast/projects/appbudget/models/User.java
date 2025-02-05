package ast.projects.appbudget.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Budget> budgets;

	public User() {}

    public User(String name, String surname) {
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
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
   
	public void setBudgets(List<Budget> budgets) {
		this.budgets = budgets;
	}
	
    @Override
    public String toString() {
    	if(name != null && surname != null) {
    		return name + " " + surname;
    	}
    	else {
    		return "Name or surname not valid";
    	}
        
    }
}
