package ast.projects.appbudget.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name= "title", nullable = false, unique = true)
    private String title;
    
    @Column(name = "totalIncomes", nullable = false)
    private double totalIncomes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenceItem> expenceItems;

    public Budget() {}
    
    public Budget(String title, double totalIncomes) {
		this.title = title;
		this.totalIncomes = totalIncomes;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getTotalIncomes() {
		return totalIncomes;
	}

	public void setTotalIncomes(double totalIncomes) {
		this.totalIncomes = totalIncomes;
	}

	@Override
    public String toString() {
        return title + " - Total Incomes: " + totalIncomes;
    }
	
	
}

