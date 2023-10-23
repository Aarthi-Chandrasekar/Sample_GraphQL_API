package com.springboot.graphql.resolver;

import com.springboot.graphql.model.Department;
import com.springboot.graphql.model.Employee;
import com.springboot.graphql.repository.DepartmentRepository;
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
public class DepartmentQueryResolver implements GraphQLQueryResolver {

    DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentQueryResolver(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Iterable<Department> departments(DataFetchingEnvironment environment) {
        DataFetchingFieldSelectionSet s = environment.getSelectionSet();
        List<Specification<Department>> specifications = new ArrayList<>();
        if (s.contains("employees"))
            return departmentRepository.findAll(fetchDoctors());

        else
            return departmentRepository.findAll();
    }

    public Department department(Integer id, DataFetchingEnvironment environment) {
        Specification<Department> spec = byId(id);
        DataFetchingFieldSelectionSet selectionSet = environment.getSelectionSet();
        if (selectionSet.contains("employees"))
            spec = spec.and(fetchDoctors());
        return departmentRepository.findOne(spec).orElseThrow(NoSuchElementException::new);
    }



    private Specification<Department> fetchDoctors() {
        return (Specification<Department>) (root, query, builder) -> {
            Fetch<Department, Employee> f = root.fetch("employees", JoinType.LEFT);
            Join<Department, Employee> join = (Join<Department, Employee>) f;
            return join.getOn();
        };
    }

    private Specification<Department> byId(Integer id) {
        return (Specification<Department>) (root, query, builder) -> builder.equal(root.get("id"), id);
    }

}
