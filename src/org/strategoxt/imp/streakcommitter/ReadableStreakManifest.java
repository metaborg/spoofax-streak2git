/**
 * 
 */
package org.strategoxt.imp.streakcommitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 * 
 */
public class ReadableStreakManifest {
	protected static final String PROP_TOTAL_STREAKS = "numstreaks";

	protected static final String PROP_PREFIX_STREAK = "streak.";

	protected static final String PROP_SUFFIX_FILES = ".files";

	private File manifestFile;

	private Properties props;

	public ReadableStreakManifest(String streaksdir) {
		assert streaksdir != null;
		final File streaksDir = new File(streaksdir);
		assert streaksDir.exists();

		manifestFile = new File(streaksDir, "streaks.mf").getAbsoluteFile();
	}

	public void load() throws IOException {
		FileInputStream fis = null;
		try {
			if (!manifestFile.exists()) {
				manifestFile.createNewFile();
			}
			fis = new FileInputStream(manifestFile);
			props = new Properties();
			props.load(fis);
		} finally {
			if (fis != null)
				fis.close();
		}
	}

	public int getNumStreaks() {
		return Integer.parseInt((String) props.get(PROP_TOTAL_STREAKS));
	}
	
	public String[] getAffectedFiles(int streaknum){
		assert streaknum > 0 && streaknum <= getNumStreaks();
		String filesStr = (String) props.get(PROP_PREFIX_STREAK + streaknum + PROP_SUFFIX_FILES);
		String[] files = filesStr.split(",");
		return files;
	}

}
