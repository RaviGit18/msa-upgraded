package com.payroll.microservices.employeepayrollservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="zuul-edge-server")
public interface EmployeeService {

	@GetMapping("/employee-service/employee/{empId}")
	public EmployeePayroll getEmployeeDetails(@PathVariable("empId") Long empId);
}
