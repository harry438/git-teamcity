package org.hivedb.teamcity.plugin;

import java.io.File;
import java.util.*;
import java.io.IOException;

import jetbrains.buildServer.util.*;
import jetbrains.buildServer.vcs.*;
import jetbrains.buildServer.CollectChangesByIncludeRule;
import jetbrains.buildServer.agent.vcs.CheckoutOnAgentVcsSupport;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.BuildProgressLogger;

import org.apache.log4j.Logger;

public class GitVcsOnAgent implements CheckoutOnAgentVcsSupport {
  Logger log = Logger.getLogger(GitVcsOnAgent.class);

  private static final String GIT_COMMAND = "git_command";
  private static final String CLONE_URL = "clone_url";

  public GitVcsOnAgent(BuildAgentConfiguration agentConf) {
  }

  public void updateSources(BuildProgressLogger logger, File workingDirectory, VcsRoot root, String newVersion, IncludeRule includeRule)
   throws VcsException {
    log.info("updateSources: " + workingDirectory + " " + newVersion);
    if (!git(root, workingDirectory.getParentFile(), workingDirectory).isGitRepo(workingDirectory.getAbsolutePath())) {
      /* TeamCity creates this directory for us, nice of them... Git dislikes that though. */
      workingDirectory.delete();
      git(root, workingDirectory.getParentFile(), workingDirectory).clone(root.getProperty(CLONE_URL));
    }
    else {
      git(root, workingDirectory.getParentFile(), workingDirectory).fetch();
    }
  }

  public String getName() {
    return "git";
  }

  private String[] getGitCommand(VcsRoot vcsRoot) {
    String path = vcsRoot.getProperty(GIT_COMMAND);
    if (new File(path).exists()) {
      return new String[] { path };
    }
    return new String[] { "/usr/bin/git" };
  }

  private Git git(VcsRoot root, File workingDirectory, File projectDirectory) {
    return new Git(getGitCommand(root), workingDirectory.getAbsolutePath(), projectDirectory.getAbsolutePath());
  }
}