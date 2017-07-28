import java.io.IOException;

public class Main {
	public static void main(String args[]) throws IOException {
		if (args.length < 2) {
			System.out.println("Provide two arguments <path to store links><starting link>");
			System.exit(1);
		}
		new NewInstance(args);
	}
}
