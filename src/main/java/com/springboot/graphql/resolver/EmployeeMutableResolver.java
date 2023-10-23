package com.springboot.graphql.resolver;
import com.springboot.graphql.model.Department;
import com.springboot.graphql.model.Employee;
import com.springboot.graphql.repository.DepartmentRepository;
import com.springboot.graphql.repository.EmployeeRepository;
import com.springboot.graphql.request.EmployeeInput;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMutableResolver implements GraphQLMutationResolver {


    EmployeeRepository employeeRepository;
    DepartmentRepository departmentRepository;



    @Autowired
    public EmployeeMutableResolver(EmployeeRepository doctorRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = doctorRepository;
        this.departmentRepository = departmentRepository;

    }

    public Employee newEmployee(EmployeeInput EmployeeInput) {
        Department department = departmentRepository.findById(EmployeeInput.getDepartmentId()).get();
        return employeeRepository.save(new Employee(null, EmployeeInput.getFirstName(), EmployeeInput.getLastName(),
                EmployeeInput.getPosition(), EmployeeInput.getAge(), EmployeeInput.getSalary(),
                EmployeeInput.getBirthday(),
                department));
    }

    public Employee updateEmployee(Integer id, EmployeeInput EmployeeInput) {
        Department department = departmentRepository.findById(EmployeeInput.getDepartmentId()).get();

        return employeeRepository.save(new Employee(id, EmployeeInput.getFirstName(), EmployeeInput.getLastName(),
                EmployeeInput.getPosition(), EmployeeInput.getAge(), EmployeeInput.getSalary(),
                EmployeeInput.getBirthday(),
                department));
    }

    public boolean deleteEmployee(Integer id){
        employeeRepository.deleteById(id);
        return true;
    }

}
