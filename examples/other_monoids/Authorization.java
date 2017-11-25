package other_monoids;

import ourlib.nonapp.AuthorizationAPI;
import ourlib.nonapp.File;
import ourlib.nonapp.Phone;

public class Authorization {
	void main() {
		File file = new File("file.txt");
		Phone phone = new Phone("phonebook.txt");
		
		AuthorizationAPI.authFile();
		file.access(); // OK
		
		AuthorizationAPI.authFile();
		AuthorizationAPI.withdrawAuth();
		file.access(); // BAD
		
		AuthorizationAPI.authPhone();
		phone.access(); // OK
	}
	
	void bad1() {
		File file = new File("file.txt");
		
		AuthorizationAPI.authFile();
		file.access(); // OK
		
		AuthorizationAPI.withdrawAuth(); // BAD
		// file.access(); // BAD
	}
	
	void bad2() {
		File file = new File("file.txt");		
		
		AuthorizationAPI.authFile();
		file.access(); // OK
		
		file.access(); // BAD
	}
	
	void ok() {
		File file = new File("file.txt");
		Phone phone = new Phone("phonebook.txt");
		
		AuthorizationAPI.authFile();
		file.access();		

		AuthorizationAPI.authPhone();
		phone.access();
		
		AuthorizationAPI.authFile();
		AuthorizationAPI.withdrawAuth();
		
		AuthorizationAPI.authPhone();
		AuthorizationAPI.authFile();
		phone.access(); 
		file.access();
	}
}
