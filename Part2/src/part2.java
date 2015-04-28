


public class part2 {
	static {
		System.loadLibrary("part2");
	}
	
	int j;
	int i;
	
	public part2() {
		i = 3;
		j = 2;
	}
	
	public native void init();
	
	public void printvar(){
		System.out.println("La valeur de j :"+j);
	    System.out.println("la valeur de i :"+i);
	}
	
	public static void main(String args[]){
		part2 O = new part2();
		O.printvar();
		O.init();
	}

	
}
