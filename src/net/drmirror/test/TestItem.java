package net.drmirror.test;

public class TestItem {

	public String firstName, lastName, email, phone, country;
	
	public TestItem (String firstName,
			         String lastName,
			         String email,
			         String phone,
			         String country) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.country = country;
	}
	
	public String toString() {
		return firstName;
	}
}
