package com.springboot.graphql.resolver;

import com.springboot.graphql.filter.EmployeeFilter;
import com.springboot.graphql.filter.FilterField;
import com.springboot.graphql.model.Department;
import com.springboot.graphql.model.Employee;
import com.springboot.graphql.repository.EmployeeRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class EmployeeQueryResolver implements GraphQLQueryResolver {

    EmployeeRepository doctorRepository;

    @Autowired
    public EmployeeQueryResolver(EmployeeRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Iterable<Employee> employees(DataFetchingEnvironment environment) {

        DataFetchingFieldSelectionSet s = environment.getSelectionSet();
        List<Specification<Department>> specifications = new ArrayList<>();
        if (s.contains("department"))
            return doctorRepository.findAll(fetchDepartment());
        else
            return doctorRepository.findAll();
    }

    public Employee employee(Integer id, DataFetchingEnvironment environment) {

        Specification<Employee> spec = byId(id);
        DataFetchingFieldSelectionSet selectionSet = environment.getSelectionSet();
        if (selectionSet.contains("department"))
            spec = spec.and(fetchDepartment());
        return doctorRepository.findOne(spec).orElseThrow(NoSuchElementException::new);

    }


    public Iterable<Employee> employeesWithFilter(EmployeeFilter filter) {
        Specification<Employee> spec = null;
        if (filter.getSalary() != null)
            spec = bySalary(filter.getSalary());
        if (filter.getBirthday() != null)
            spec = (spec == null ? byBirthday(filter.getBirthday()) : spec.and(byBirthday(filter.getBirthday())));
        if (filter.getAge() != null)
            spec = (spec == null ? byAge(filter.getAge()) : spec.and(byAge(filter.getAge())));
        if (filter.getPosition() != null)
            spec = (spec == null ? byPosition(filter.getPosition()) :
                    spec.and(byPosition(filter.getPosition())));
        if (spec != null)
            return doctorRepository.findAll(spec);
        else
            return doctorRepository.findAll();
    }



    private Specification<Employee> fetchDepartment() {
        return (Specification<Employee>) (root, query, builder) -> {
            Fetch<Employee, Department> f = root.fetch("department", JoinType.LEFT);
            Join<Employee, Department> join = (Join<Employee, Department>) f;
            return join.getOn();
        };
    }

    private Specification<Employee> byId(Integer id) {
        return (Specification<Employee>) (root, query, builder) -> builder.equal(root.get("id"), id);
    }

    private Specification<Employee> bySalary(FilterField filterField) {
        return (Specification<Employee>) (root, query, builder) -> filterField.generateCriteria(builder, root.get("salary"));
    }

    private Specification<Employee> byAge(FilterField filterField) {
        return (Specification<Employee>) (root, query, builder) -> filterField.generateCriteria(builder, root.get("age"));
    }

    private Specification<Employee> byPosition(FilterField filterField) {
        return (Specification<Employee>) (root, query, builder) -> filterField.generateCriteria(builder, root.get("position"));
    }

    private Specification<Employee> byBirthday(FilterField filterField) {
        return (Specification<Employee>) (root, query, builder) -> filterField.generateCriteria(builder, root.get("birthday"));
    }

}
