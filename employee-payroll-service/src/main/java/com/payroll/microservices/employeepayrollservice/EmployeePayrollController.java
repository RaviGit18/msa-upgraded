package com.payroll.microservices.employeepayrollservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
public class EmployeePayrollController {

	@Autowired
	EmployeePayrollRepository employeePayrollRepository;
	
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	RoleService roleService;
	
	private static Logger log = LoggerFactory.getLogger(EmployeePayrollController.class);
	
	@PostMapping("/employee/{empId}/role/{roleName}")
	public EmployeePayroll insertEmployeePayrollDetails(@PathVariable Long empId, @PathVariable String roleName) {
	
	//public void insertEmployeePayrollDetails(@PathVariable Long empId, @PathVariable String roleName) {
		
		
		log.info("Inside EmployeePayrollController::insertEmployeePayrollDetails()");
		
	
		/*
		 * EmployeePayroll employeePayroll = new EmployeePayroll(1L,1000L,"AAA1","BBB1",
		 * 100L,"HR","Human Resource"); employeePayrollRepository.save(employeePayroll);
		 */
		
		
		/*
		 * ResponseEntity<EmployeePayroll> forEntity = new RestTemplate()
		 * .getForEntity("http://localhost:8080/employee/{empId}",
		 * EmployeePayroll.class, empId); ResponseEntity<EmployeePayroll> roleEntity =
		 * new RestTemplate() .getForEntity("http://localhost:8081/role/{roleName}",
		 * EmployeePayroll.class, roleName); EmployeePayroll employeePayroll =
		 * forEntity.getBody();
		 * employeePayroll.setRoleId(roleEntity.getBody().getRoleId());
		 * employeePayroll.setRoleName(roleEntity.getBody().getRoleName());
		 * employeePayroll.setDescription(roleEntity.getBody().getDescription());
		 * employeePayrollRepository.save(employeePayroll);
		 */
		
		EmployeePayroll employeePayroll = employeeService.getEmployeeDetails(empId);
		EmployeePayroll roleDetails = roleService.getRoleByRoleName(roleName);
		
		employeePayroll.setRoleId(roleDetails.getRoleId());
		employeePayroll.setRoleName(roleDetails.getRoleName());
		employeePayroll.setDescription(roleDetails.getDescription());
		employeePayrollRepository.save(employeePayroll);
		
		return employeePayroll;
		
		 
	}
}
