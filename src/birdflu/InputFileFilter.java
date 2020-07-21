package birdflu;

import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;

public class InputFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		if(pathname.isDirectory() || pathname.isHidden()) {
			Logger.getLogger("File Logger").finer
			("Filtered out directory or hidden file: " + pathname.getName());
			return false;
		}
		if(pathname.getName().endsWith(".txt")) {
			Logger.getLogger("File Logger").finer("Accepted .txt file for input: "
					+ pathname.getName());
			return true;
		}
		Logger.getLogger("File Logger").finer("Filtered out unknown filetype: "
				+ pathname.getName());
		return false;
	}
}