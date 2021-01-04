
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		PathFinder run = new PathFinder("Template1.dat", "Template2.dat", "Template3.dat");
		for (int i = 0; i <= 1; i++) {
			run.repeat();
		}
	}
}

