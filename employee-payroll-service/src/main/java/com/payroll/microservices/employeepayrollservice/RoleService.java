package com.payroll.microservices.employeepayrollservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="zuul-edge-server")
public interface RoleService {

	@GetMapping("/role-service/role/{roleName}")
	public EmployeePayroll getRoleByRoleName(@PathVariable("roleName") String roleName);
}
