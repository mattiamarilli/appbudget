package ast.projects.appbudget.models;

import javax.persistence.*;

@Entity
@Table(
    name = "expenseitems",
    uniqueConstraints = @UniqueConstraint(columnNames = {"title", "type", "budget_id"})
)
public class ExpenseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "budget_id")
    private long budgetId;

    public ExpenseItem() {}

    public ExpenseItem(String title, Type type, double amount) {
        this.title = title;
        this.type = type;
        this.amount = amount;
    }
    
    public ExpenseItem(long id,String title, Type type, double amount) {
    	this.id = id;
        this.title = title;
        this.type = type;
        this.amount = amount;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getBudgetId() {
		return budgetId;
	}

	public void setBudgetId(long budgetId) {
		this.budgetId = budgetId;
	}

	@Override
    public String toString() {
        return title + " - " + amount + " - " + type;
    }
}
