package com.pegg.maven.plugin.mojo;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.common.io.ByteStreams;

import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.utils.values.ConfigManager;
import me.superblaubeere27.jobf.utils.values.Configuration;

@Mojo(name = "obfus")
public class ObfuscatorMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject mavenProject;

	@Parameter(property = "inputJar")
	private String inputJar;

	@Parameter(property = "outputJar")
	private String outputJar;

	@Parameter(property = "scriptFile")
	private File scriptFile;

	@Parameter(property = "config")
	private File configFile;

	@Parameter(property = "threads")
	private int threads;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		Log log = getLog();
		log.info("Start Obfuscator......");

		try {
			Configuration configuration = null;
			String config = null;
			if (this.configFile != null && this.configFile.isFile() && this.configFile.exists()) {
				config = new String(Files.readAllBytes(this.configFile.toPath()));
			} else {
				try (InputStream in = this.getClass().getResourceAsStream("default.conf.json")) {
					config = new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);
				}
			}
			log.info("Obfuscator configuration......");
			log.info(config);
			configuration = ConfigManager.loadConfig(config);

			boolean defaultFlag = false;
			String inputJar = configuration.getInput();
			if (isBlank(inputJar)) {
				if (!(defaultFlag = isBlank(this.inputJar))) {
					inputJar = this.inputJar;
				}
			}
			String outputJar = configuration.getOutput();
			if (isBlank(outputJar)) {
				if (!(defaultFlag = isBlank(this.outputJar))) {
					outputJar = this.outputJar;
				}
			}
			if (defaultFlag) {
				Build build = mavenProject.getBuild();
				String outputDirectory = build.getDirectory();
				String jarFinalName = build.getFinalName();
				String jarPackaging = this.mavenProject.getPackaging();

				if (isBlank(inputJar)) {
					inputJar = outputDirectory + File.separator + jarFinalName + "." + jarPackaging;
					configuration.setInput(inputJar);
				}
				if (isBlank(outputJar)) {
					outputJar = outputDirectory + File.separator + jarFinalName + ".obfus." + jarPackaging;
					configuration.setOutput(outputJar);
				}
			}
			log.info("InputJar: " + inputJar);
			log.info("OutputJar: " + outputJar);

			String script = null;
			if (null != this.scriptFile && this.scriptFile.isFile() && this.scriptFile.exists()) {
				script = new String(Files.readAllBytes(this.scriptFile.toPath()));
				if (isBlank(script)) {
					configuration.setScript(script);
				}
			}

			if (this.threads > 0 && this.threads <= Runtime.getRuntime().availableProcessors()) {
				JObfImpl.INSTANCE.setThreadCount(threads);
			}
			
			JObfImpl.INSTANCE.processJar(configuration);

			log.info("End Obfuscator......");
		} catch (Exception e) {
			log.error("Obfuscated file exception", e);
		}
	}

	private boolean isBlank(String inputJar) {
		return null == inputJar || inputJar.isEmpty();
	}

}
