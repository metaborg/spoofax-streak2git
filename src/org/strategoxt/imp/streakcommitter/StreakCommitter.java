/**
 * 
 */
package org.strategoxt.imp.streakcommitter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 * 
 */
public class StreakCommitter {
	public final static String CLI_HELP = "--help";
	public final static String CLI_PROJ = "--project";
	public final static String CLI_STREAKSDIR = "--streaks-dir";

	public static void main(String[] args) throws IOException {
		final Collection<String> argsl = Arrays.asList(args);

		String project = new File(".").getCanonicalPath();
		String streaksdir = null;

		final Iterator<String> argsIter = argsl.iterator();
		while (argsIter.hasNext()) {
			String arg = argsIter.next();
			if (arg.equals(CLI_HELP)) {
				System.out.println(help());
				System.exit(0);
			} else if (arg.equals(CLI_PROJ)) {
				if (!argsIter.hasNext()) {
					System.err.println("Missing argument for " + CLI_PROJ);
					System.out.println(help());
					System.exit(1);
				}
				final File projFile = new File(argsIter.next());
				if (!projFile.exists()) {
					throw new IllegalArgumentException("Project directory does not exist " + projFile.getAbsolutePath());
				}
				project = projFile.getCanonicalPath();

			} else if (arg.equals(CLI_STREAKSDIR)) {
				if (!argsIter.hasNext()) {
					System.err.println("Missing argument for " + CLI_STREAKSDIR);
					System.out.println(help());
					System.exit(1);
				}
				streaksdir = argsIter.next();
			} else {
				System.err.println("Unknown argument " + arg);
				System.out.println(help());
				System.exit(1);
			}
		}

		if (streaksdir == null) {
			final File streaksdirFile = new File(new File(project), "_recordedstreaks");
			if (!streaksdirFile.exists()) {
				throw new IllegalArgumentException("Streaks directory does not exist " + streaksdir);
			}
			streaksdir = streaksdirFile.getCanonicalPath();
		} else {
			final File streaksdirFile = new File(new File(project), streaksdir);
			if (!streaksdirFile.exists()) {
				throw new IllegalArgumentException("Streaks directory does not exist " + streaksdir);
			}
			streaksdir = streaksdirFile.getCanonicalPath();
		}

		final Committer proc = new Committer(project, streaksdir);
		try {
			proc.perform();
			System.out.println("Done!");
			System.exit(0);
		} catch (CommitterException e) {
			System.err.println("Errors occurred");
			e.printStackTrace();
			System.exit(1);

		}
	}

	private static String help() {
		// @formatter:off
		final String helpMsg = 
				"[--pretend] [--project] [--create-repo] [--streaks-dir] [--help] \n" + 
				"	--project		Override the project directory. Otherwise assumed current directory.\n" +
				"	--streaks-dir	Override the streaks directory in the project. Otherwise assumed _recordedstreaks.\n" +
				"	--help			This help messages.";
		// @formatter:on
		return helpMsg;
	}

}
