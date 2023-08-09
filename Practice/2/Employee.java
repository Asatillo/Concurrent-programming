import java.util.List;
import java.util.ArrayList;

interface SalariedEntity{
    public int getSalary();
}

class Subcontractor implements SalariedEntity{
    private long taxNumber;
    private int salary;
    
    public Subcontractor(long taxNumber, int salary){
        this.taxNumber = taxNumber;
        this.salary = salary;
    }
    
    public int getSalary(){
        return salary;
    }
}

abstract public class Employee implements SalariedEntity{
    private String name;
    protected int salary;
    public Employee(String name, int salary){
        this.name = name;
        this.salary = salary;
    }
    
    public String getName(){ return name; }
    abstract public int getSalary();
    public void raiseSalary(float percent){
        salary *= (1 + percent);
    }
    
}

class Manager extends Employee{
    private List<Employee> employees;
    public Manager(String name, int salary){
        super(name, salary);
        employees = new ArrayList<Employee>();
    }
    public void addEmployee(Employee e){
        employees.add(e);
    }
    
    public void removeEmployee(Employee e){
        employees.remove(e);
    }
    
    public int getSalary(){
        return salary + (int)(employees.stream().mapToInt(x -> x.getSalary()).sum() * 0.05);
    }
}

class Subordinate extends Employee{
    public Subordinate(String name, int salary){
        super(name, salary);
    }
    public int getSalary(){ return salary; }
}

class Company{
    private ArrayList<SalariedEntity> entities;

    public Company(){
        ArrayList<SalariedEntity> entities = new ArrayList<>();
    }
    public void addEntity(SalariedEntity e){
        entities.add(e);
    }

    public void removeEntity(SalariedEntity e){
        entities.remove(e);
    }

    public void raiseSalaries(long percent){
        for (SalariedEntity entity : entities) {
            if(entity instanceof Employee){
                entity.raiseSalary(percent);
            }
        }
    }
}
class EmployeeTest{
    public static void main(String[] args) {
        Manager emp1 = new Manager("Joe", 1000);
        Subordinate sub1 = new Subordinate("Mike", 100);
        Subordinate sub2 = new Subordinate("Sarah", 200);
        emp1.addEmployee(sub1);
        emp1.addEmployee(sub2);
        System.out.println("Name: " + emp1.getName() + " Salary: " + emp1.getSalary());
    }
}