package com.naviworks.invt.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class EmployeeDTO
{
	 private String _id;
	 private String name;
	 private String head;
	 private String department;
	 private String age;
	 private String grade;
	 private String joinYear;
	 private String number;
	 private String Phone;
	 private String Inner;
	 
	 public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	 public EmployeeDTO()
	 {
	     super();
	 }
	 
	 @JsonIgnoreProperties(ignoreUnknown = true)
	 public EmployeeDTO(String name, String head, String department)
	 {
		 this._id = _id;
	     this.name = name;
	     this.head = head;
	     this.department = department;
	     this.age = age;
	     this.grade = grade;
	     this.joinYear = joinYear;
	     this.number = number;
	     this.Phone = Phone;
	     this.Inner = Inner;
	 }
	 
	 
}
