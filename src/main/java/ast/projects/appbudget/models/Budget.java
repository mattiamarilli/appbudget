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
    
    @Column(name = "incomes", nullable = false, unique = true)
    private double incomes;
    
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseItem> expenseItems;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

	public Budget() {}
	
    public Budget(String title, double incomes) {
		this.title = title;
		this.incomes = incomes;
	}

	public String getTitle() {
		return title;
	}
	
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

	public void setTitle(String title) {
		this.title = title;
	}

	public double getIncomes() {
		return incomes;
	}

	public void setIncomes(double incomes) {
		this.incomes = incomes;
	}

	public List<ExpenseItem> getExpenseItems() {
		return expenseItems;
	}

	public void setExpenseItems(List<ExpenseItem> expenseItems) {
		this.expenseItems = expenseItems;
	}

	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}



	@Override
    public String toString() {
        return title + " - " + incomes + "$";
    }
    

}
