package com.payroll.microservices.employeeservice;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix="employee-service")
@Component
public class EmployeeConfiguration {

	private String defaultFirstName;
	private String defaultLastName;
	public String getDefaultFirstName() {
		return defaultFirstName;
	}
	public void setDefaultFirstName(String defaultFirstName) {
		this.defaultFirstName = defaultFirstName;
	}
	public String getDefaultLastName() {
		return defaultLastName;
	}
	public void setDefaultLastName(String defaultLastName) {
		this.defaultLastName = defaultLastName;
	}
	
}
