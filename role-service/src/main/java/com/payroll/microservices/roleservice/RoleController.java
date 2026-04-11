package com.payroll.microservices.roleservice;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RoleController {
	
	@Autowired
	EmployeeRoleRepository employeeRoleRepository;
	
	private static Logger log = LoggerFactory.getLogger(RoleController.class);
	
	@GetMapping("/role/{roleName}")
	public EmployeeRole getRoleByRoleName(@PathVariable String roleName) {
		
		log.info("Inside RoleController::getRoleByRoleName()");
		
		//return new EmployeeRole(10L,"HR","Human Resource");
		
		EmployeeRole employeeRole = employeeRoleRepository.findByRoleName(roleName);
		return employeeRole;
	}

}
