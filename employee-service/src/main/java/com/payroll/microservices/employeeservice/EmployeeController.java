package com.payroll.microservices.employeeservice;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


@RestController
public class EmployeeController {
	
	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	Environment environment;
	
	@Autowired
	EmployeeConfiguration employeeConfiguration;
	
	private static Logger log = LoggerFactory.getLogger(EmployeeController.class);
	
	@GetMapping("/employee/{empId}")
	public Employee getEmployeeDetails(@PathVariable Long empId) {
		
		log.info("Inside EmployeeController::getEmployeeDetails()");
		
		//return new Employee("FNAME","LNAME",101L,new Date());
		
		Employee employee = employeeRepository.findById(empId).orElse(null);
		
		employee.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
		
		return employee;
	}
	

	@GetMapping("/employee/fault-tolerance")
	@CircuitBreaker(name = "employeeService", fallbackMethod="fallbackEmployeeDetails")
	public Employee getEmployeeDetailsFaultTolerance() {
		throw new RuntimeException("Some Issue");
	}
	
	public Employee fallbackEmployeeDetails(Exception ex){
		//return new Employee("FNAME","LNAME",101L,new Date());
		
		return new Employee(employeeConfiguration.getDefaultFirstName(),employeeConfiguration.getDefaultLastName(),101L,new Date());
	}
	
	
}
