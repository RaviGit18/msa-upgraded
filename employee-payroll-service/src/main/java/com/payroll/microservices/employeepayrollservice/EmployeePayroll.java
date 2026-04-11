package com.payroll.microservices.employeepayrollservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class EmployeePayroll {

	@Id
	@Column(name="payroll_Id")
	@GeneratedValue
	private long payrollId;
	
	@Column(name="emp_Id")
	private Long empId;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="role_Id")
	private Long roleId;
	
	@Column(name="role_name")
	private String roleName;
	
	@Column(name="role_description")
	private String description;
	
	private int port;
	
	public EmployeePayroll(long payrollId, Long empId, String firstName, String lastName, Long roleId, String roleName,
			String description) {
		super();
		this.payrollId = payrollId;
		this.empId = empId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.roleId = roleId;
		this.roleName = roleName;
		this.description = description;
	}

	public EmployeePayroll() {
		
	}

	public long getPayrollId() {
		return payrollId;
	}

	public void setPayrollId(long payrollId) {
		this.payrollId = payrollId;
	}

	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
}
