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
import javax.persistence.UniqueConstraint;

@Entity

@Table(
	name = "budgets",
    uniqueConstraints = @UniqueConstraint(columnNames = {"title", "user_id"})
)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name= "title", nullable = false)
    private String title;
    
    @Column(name = "incomes", nullable = false)
    private double incomes;
    
    @OneToMany(mappedBy = "budgetId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ExpenseItem> expenseItems;
    
    @Column(name = "user_id", nullable = false)
    private long userId;

	public Budget() {}
	
    public Budget(String title, double incomes) {
		this.title = title;
		this.incomes = incomes;
	}
    
    public Budget(long id, String title, double incomes) {
		this.id = id;
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setIncomes(double incomes) {
		this.incomes = incomes;
	}

	public void setExpenseItems(List<ExpenseItem> expenseItems) {
		this.expenseItems = expenseItems;
	}

	@Override
    public String toString() {
		if(title != null)
		{
			return title + " - " + incomes + "$";
		}
		else
			return "Titol not valid";
    }
    

}
