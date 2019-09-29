package function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import gui.MessageBox;

// Get checksum from runtime
public class Sum {

	// Input hash algorithm, file path, operating system
	// Output checksum or show an exception
	public static String getSum(String algo, String filePath, String os) {
		
		// Create the OS specific runtime command
		String[] cmd = {};

		switch(os) {
		case "OSX":
			if (algo.startsWith("SHA")) {
				cmd = new String[4];
				cmd[0] = "SHASUM";
				cmd[1] = "-a";
				cmd[2] = algo.substring(3);
				cmd[3] = filePath;

			} else {
				cmd = new String[3];
				cmd[0] = algo.substring(0, 3);
				cmd[1] = "-q";
				cmd[2] = filePath;
			}
			break;

		case "LINUX":
			cmd = new String[2];
			cmd[0] = algo.toLowerCase() + "sum";
			cmd[1] = filePath;
			break;
			
		case "WIN":
			cmd = new String[4];
			cmd[0] = "certutil";
			cmd[1] = "-hashfile";
			cmd[2] = filePath;
			cmd[3] = algo;
			break;
		}

		// Run command in runtime
		String checksum = null;
		try {
			Process p;
			p = Runtime.getRuntime().exec(cmd);

			// Create reader for the process
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((checksum = input.readLine()) == null) {
			}

			// Extract the checksum from buffer
			switch(os) {
			case "OSX":
				if (algo.startsWith("SHA")) {
					checksum = checksum.substring(0, checksum.indexOf(" "));
				} else {
					return checksum;
				}
				break;
				
			case "LINUX":
				checksum = checksum.substring(0, checksum.indexOf(" "));
				break;
				
			case "WIN":
				// In Windows the checksum is the second line of the process output
				checksum = null;
				while ((checksum = input.readLine()) == null) {
				}
				break;
			}
		
		// Show exception
		} catch (IOException e) {
			MessageBox.display("Exception!", e.getMessage());;
		}

		return checksum;
	}
}
