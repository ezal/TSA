package simple_ones;

import ourlib.nonapp.TaintAPI;



class ListNode {
	String x;
	ListNode next;
	
	static int len = 17;
	static int cnt = 0;

	static void a0(ListNode crt) {
		if (cnt++ == len) return;
		else {
			crt.x = TaintAPI.getTaintedString();
			crt.next = new ListNode();
			a1(crt.next);
		}
	}

	static void a1(ListNode crt) {
		if (cnt++ == len) return;
		else {
			crt.x = "untainted";
			crt.next = new ListNode();
			a2(crt.next);
		}
	}
	static void a2(ListNode crt) {
		if (cnt++ == len) return;
		else {
			crt.x = "untainted";
			crt.next = new ListNode();
			a3(crt.next);
		}
	}
	static void a3(ListNode crt) {
		if (cnt++ == len) return;
		else {
			crt.x = "untainted";
			crt.next = new ListNode();
			a4(crt.next);
		}
	}
	static void a4(ListNode crt) {
		if (cnt++ == len) return;
		else {
			crt.x = "untainted";
			crt.next = new ListNode();
			a5(crt.next);
		}
	}
	static void a5(ListNode crt) {
		if (cnt++ == len) return;
		else {
			crt.x = "untainted";
			crt.next = new ListNode();
			a6(crt.next);
		}
	}
	static void a6(ListNode crt) {
		if (cnt++ == len) return;
		else {
			crt.x = "untainted";
			crt.next = new ListNode();
			a7(crt.next);
		}
	}
	static void a7(ListNode crt) {
		if (cnt++ == len) return;
		else {
			crt.x = "untainted";
			crt.next = new ListNode();
			a8(crt.next);
		}
	}
	static void a8(ListNode crt) {
		if (cnt++ == len) return;
		else {
			crt.x = "untainted";
			crt.next = new ListNode();
			a9(crt.next);
		}
	}
	static void a9(ListNode crt) {
		if (cnt++ == len) return;
		else {
			crt.x = "untainted";
			crt.next = new ListNode();
			a0(crt.next);
		}
	}



	static void b(ListNode crt) {		
		TaintAPI.outputString(crt.x);
		if (crt.next != null) 
			c(crt.next);
	}

	static void c(ListNode crt) {
		if (crt.next != null) 
			b(crt.next);
	}



	void bad() {
		ListNode head = new ListNode();
		a0(head);
		b(head); /* BAD */
	}
	
	void ok() {
		ListNode head = new ListNode();
		a0(head);
		c(head); /* OK */
	}


	public static void main(String[] args) {
		System.out.println("building the list");
		ListNode head = new ListNode();		
		a0(head);
		
		ListNode.cnt = 0;
		System.out.println("\nrunning b:");
		b(head); /* OK */
		
		ListNode.cnt = 0;
		System.out.println("\nrunning c:");
		c(head); /* BAD */
	}
}
