package other_monoids;



import ourlib.nonapp.AuthAPI;

public class Auth {

	public void main() {
		String s;

		while (true) {
			try {
				AuthAPI.auth();
				s = AuthAPI.access();
			} catch (Exception e) {
				s = "Invalid Access";
			}
			AuthAPI.output(s);
		}
	}
}

