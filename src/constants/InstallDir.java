package constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class InstallDir {

	private static Path INSTALL_DIR = Paths.get("/home/jeff/Storage/git/ThriftyClone/");
	
	public static Path getInstallDir() {
		return INSTALL_DIR;
	}
	
	public static void setInstallDir(Path path) {
		INSTALL_DIR = path;
	}
	
}
