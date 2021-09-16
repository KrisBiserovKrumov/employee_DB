import entities.Address;
import entities.Employee;
import entities.Town;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Engine implements Runnable {

    private final EntityManager entityManager;
    private  final BufferedReader reader;

    public Engine(EntityManager entityManager) {

        this.entityManager = entityManager;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        //Ex 1
        //this.removeObjectsEx();
        //Ex 2
       //try {
       //    this.containsEmployeeEx();
       //} catch (IOException e) {
       //    e.printStackTrace();
       //}
        //Ex 3
        //this.employeeWithSalaryOver50000();
        //Ex4
        //this.employeesFromDepartmentsEx();
        //Ex5
        try {
            this.addingNewAddressAndAddItToEmployee();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addingNewAddressAndAddItToEmployee() throws IOException {
        System.out.println("Enter employee last name:");
        String lastName = reader.readLine();
        Employee employee = this.entityManager.createQuery(
                "SELECT e FROM Employee AS e " +
                        "WHERE e.lastName =:name",Employee.class
        ).setParameter("name", lastName).getSingleResult();

        Address address = this.createNewAddress("Vitoshka 15");

        this.entityManager.getTransaction().begin();
        this.entityManager.detach(employee);
        employee.setAddress(address);
        this.entityManager.merge(employee);
        this.entityManager.flush();
        this.entityManager.getTransaction().commit();
    }

    private Address createNewAddress(String textContent) {
        Address address = new Address();
        address.setText(textContent);
        this.entityManager.getTransaction().begin();
        this.entityManager.persist(address);
        this.entityManager.getTransaction().commit();
        return address;
    }

    private void employeesFromDepartmentsEx() {
        List<Employee> employees = this.entityManager.createQuery(
                "SELECT e FROM Employee AS e " +
                        "WHERE e.department.name = 'Research and Development' ORDER BY e.salary, e.id",Employee.class
        ).getResultList();
        for (Employee employee : employees) {
            System.out.printf("%s %s from Research and Development - $%.2f%n"
                    ,employee.getFirstName(),employee.getLastName(), employee.getSalary());
        }

    }
    private void employeeWithSalaryOver50000() {
        List<Employee> employees = this.entityManager.createQuery(
                "SELECT e FROM Employee AS e WHERE e.salary > 50000",Employee.class
        ).getResultList();

        for (Employee employee : employees) {
            System.out.printf("%s%n",employee.getFirstName());
        }
    }
    private void containsEmployeeEx() throws IOException {
        System.out.println("Enter employee full name");
        String fullName = this.reader.readLine();
        try {
            Employee employee = this.entityManager.createQuery(
                    "SELECT e FROM Employee AS e " +
                            "WHERE concat(e.firstName , ' ', e.lastName) =:name", Employee.class)
                    .setParameter("name", fullName).getSingleResult();
            System.out.println("Yes");
        }catch (NoResultException nre){
            System.out.println("No");
        }
    }
    private void removeObjectsEx(){

        List<Town> towns = this.entityManager.createQuery("SELECT t FROM Town AS t " +
                "WHERE length( t.name) > 5", Town.class)
                .getResultList();

        this.entityManager.getTransaction().begin();

        towns.forEach(this.entityManager::detach);

        for (Town town : towns) {
            town.setName(town.getName().toLowerCase());
        }

        towns.forEach(this.entityManager::merge);
        this.entityManager.flush();
        this.entityManager.getTransaction().commit();

    }
}
