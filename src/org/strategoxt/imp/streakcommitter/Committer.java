/**
 * 
 */
package org.strategoxt.imp.streakcommitter;

import static org.strategoxt.imp.streakcommitter.Logger.log;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * @author Vlad Vergu <v.a.vergu add tudelft.nl>
 * 
 */
public class Committer {
	private final String project;
	private final String streaksdir;
	private Git git;

	public Committer(String project, String streaksdir) {
		assert project != null;
		assert streaksdir != null;
		this.project = project;
		this.streaksdir = streaksdir;
	}

	public void perform() throws CommitterException {
		log("Working in directory " + project);
		log("Loading manifest");
		final ReadableStreakManifest manifest = new ReadableStreakManifest(streaksdir);
		try {
			manifest.load();
			log("Loaded manifest");
		} catch (IOException e) {
			throw new CommitterException("Failed to load manifest", e);
		}
		final int numStreaks = manifest.getNumStreaks();
		log("Committing " + numStreaks + " streaks");
		if (numStreaks == 0) {
			return;
		}
		initRepo();
		assert git != null;
		for (int st = 1; st <= numStreaks; st++) {
			commitStreak(manifest, st);
		}
	}

	private void commitStreak(ReadableStreakManifest manifest, int st) throws CommitterException {
		System.out.println("Committing " + st);
		final File recordDir = new File(streaksdir, st + "");
		final File projectDir = new File(project);
		assert recordDir.exists();
		assert projectDir.exists();

		String[] affectedFiles = manifest.getAffectedFiles(st);
		File[] toStageFiles = new File[affectedFiles.length];
		for (int idx = 0; idx < affectedFiles.length; idx++) {
			final String f = affectedFiles[idx];
			final File srcFile = new File(recordDir, f + "._streak");
			final File dstFile = new File(projectDir, f);
			assert srcFile.exists();
			if (!dstFile.exists()) {
				dstFile.getParentFile().mkdirs();
			}
			try {
				FileUtils.copyFile(srcFile, dstFile);
			} catch (IOException e) {
				throw new CommitterException("Failed to copy file", e);
			}
			toStageFiles[idx] = dstFile;
		}

		AddCommand add = git.add();
		for (File f : toStageFiles) {
			final String relFile = projectDir.toURI().relativize(f.toURI()).getPath();
			add.addFilepattern(relFile);
		}
		try {
			add.call();
		} catch (GitAPIException e) {
			throw new CommitterException("Git stage failed", e);
		}

		CommitCommand commit = git.commit();
		commit.setMessage("Streak " + st);
		try {
			commit.call();
		} catch (GitAPIException e) {
			throw new CommitterException("Git stage failed", e);
		}

	}

	private void initRepo() throws CommitterException {
		FileRepositoryBuilder gitBuilder = new FileRepositoryBuilder();
		gitBuilder = gitBuilder.setGitDir(new File(project + "/.git")).readEnvironment().findGitDir();
		gitBuilder.setMustExist(true);
		try {
			git = new Git(gitBuilder.build());
		} catch (IOException e) {
			throw new CommitterException("Could not open Git repository", e);
		}
	}
}
