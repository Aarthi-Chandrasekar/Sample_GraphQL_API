package com.springboot.graphql.resolver;

import com.springboot.graphql.model.Department;
import com.springboot.graphql.repository.DepartmentRepository;
import com.springboot.graphql.request.DepartmentInput;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMutableResolver implements GraphQLMutationResolver {

    DepartmentRepository departmentRepository;


    @Autowired
    public DepartmentMutableResolver(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department newDepartment(DepartmentInput departmentInput) {
        return departmentRepository.save(new Department(null, departmentInput.getName(), null));
    }

    public Department updateDepartment(Integer id , DepartmentInput departmentInput) {

        Department updatedDepartment = new Department();
        updatedDepartment.setId(id);
        updatedDepartment.setName(departmentInput.getName());
        updatedDepartment.setEmployees(null);

        return departmentRepository.save(updatedDepartment);
    }

    public boolean deleteDepartment(Integer id){
        departmentRepository.deleteById(id);
        return true;
    }

}
