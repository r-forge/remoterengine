/*
 * Copyright (c) 2009, Ian Long <ilong@stoatsoftware.com>
 *
 * This file is part of the RemoteREngine project
 *
 * The RemoteREngine project is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * The RemoteREngine project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the RemoteREngine project. If not, see <http://www.gnu.org/licenses/>.
 */

package org.rosuda.REngine.remote.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.EmptyStackException;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Helper class to set the environment and then launch the server process
 * @author $Author$
 * @version $Rev$ as of $Date$
 * <p>URL : $HeadURL$
 */
public class Launcher {
	/** Key for the JAVA_HOME environment variable / system property */
	public static final String JAVA_HOME = "JAVA_HOME";
	/** Key for the system property identifying the java executable */
	public static final String JAVACMD = "JAVACMD";
	/** Key for the R_HOME environment variable / system property */
	public static final String R_HOME = "R_HOME";
	/** Key for the System property identifying the R Share directory */
	public static final String R_SHARE = "R_SHARE";
	/** Key for the System property identifying the R include directory */
	public static final String R_INCLUDE_DIR = "R_INCLUDE_DIR";
	/** Key for the System property identifying the R document directory */
	public static final String R_DOC_DIR = "R_DOC_DIR";
	/** Key for the System property / Environment variable identifying the library load path */
	public static final String LD_LIBRARY_PATH = "LD_LIBRARY_PATH";
	/** Key for the environment variable / System property identifying the locale */ 
	public static final String LC_NUMERIC = "LC_NUMERIC";
	/** Key for the System property for NO_SIG */
	public static final String NO_SIG = "NO_SIG";
	/** Key for the System property to identify the default R packages to be loaded */
	public static final String R_DEFAULT_PACKAGES = "R_DEFAULT_PACKAGES";
	
	/** Define the default class that this program will try and launch */
	public static final String DEFAULT_STARTCLASS = "org.rosuda.REngine.remote.server.REngineServer";
	/** Define the default amount of memory that will be allocated to the process launched */
	public static final String DEFAULT_MEMORYSIZE = "mx1024M";
	/** Define the default jar file to be referenced during the launch */
	public static final String SERVER_JARFILE = "RemoteREngine-server.jar";
	/** Default list of packages to be loaded when the server starts */
	public static final String[] DEFAULT_PACKAGES = new String[] {"utils", "stats", "rJava", "methods", "grDevices", 
		"graphics", "datasets"};
	/** Internal cache of the system properties */
	protected Properties systemProps = System.getProperties();
	/** Internal store for the command line arguments from the launcher */
	protected String[] args;
	/** Flag to indicate whether verbose output should be produced during launch */
	protected boolean verbose = false;
	/** Flag to indicate that the launcher should be run in test mode - i.e. not launch the process */
	protected boolean testMode = false;
	/** Warning string constructed while processing and checking the environment */
	protected StringBuilder warning = new StringBuilder();

	/**
	 * Print a list of the menu options to StdOut. This can be activated by the '-h' option on the command line
	 * @param exit Should the JVM shut down after printing the message
	 */
	private void printMenu(boolean exit) {
		StringBuilder menu = new StringBuilder();
		menu.append(this.getClass().getName() + " command line options:");
		menu.append("\nLauncher -v -h -b path -c class -XMemoryDefinition <System Properties>");
		menu.append("\n\t-h: Print this menu");
		menu.append("\n\t-v: Verbose mode; default false");
		menu.append("\n\t-X: Java process memory defintion; default " + DEFAULT_MEMORYSIZE);
		menu.append("\n\t-b: Path to root of class files (instead of " + SERVER_JARFILE + " file");
		menu.append("\n\t-c: Class to be launched; default " + DEFAULT_STARTCLASS);
		menu.append("\n\t-D<propertyname>=<propertyvalue>: Set System property");
		menu.append("\n\t-t: Test mode - write out the launch command but do not execute it");
		System.out.println(menu.toString());
		if (exit) System.exit(0);
	}
	
	/**
	 * Check the required environment settings and construct the command to launch the server.
	 * @see #printMenu(boolean)
	 * @param javaCmd Path to the java executable
	 * @return Components of the command to launch the server
	 */
	public Vector<String> buildLaunchCommand(String javaCmd) {
		String startClass = DEFAULT_STARTCLASS;
		String memorySize = DEFAULT_MEMORYSIZE;
		Properties newProperties = new Properties();

		String buildDirPath = null;		// Root directory for the build directory of the project source
		boolean useJarFiles = true;		// The alternative would use the .class files directly
		Vector<String> argVector = new Vector<String>();
		Stack<String> orginalArgs = new Stack<String>();
		for (int i = args.length-1; i >= 0; i--) { // populate the stack in reverse order
			if (args[i].equals("-v")) verbose = true;
			else if (args[i].equals("-h")) printMenu(true);
			else if (args[i].equals("-t")) testMode = true;
			else
				orginalArgs.push(args[i]);
		}
		
		// Parse the command line arguments
		while (true) {
			try {
				String arg = orginalArgs.pop();
				if (arg.equals("-b")) {
					buildDirPath = orginalArgs.pop();
					if (buildDirPath == null) {
						warning.append("Incomplete build path set using -b option");
					} else {
						File buildDir = new File(buildDirPath);
						if (buildDir.exists() && buildDir.isDirectory()) {
							useJarFiles = false;
						} else {
							warning.append("Build path set with -b option must reference a directory");
						}
					}
				} else if (arg.startsWith("-X")) memorySize = arg.substring(2);
				else if (arg.startsWith("-c")) startClass = orginalArgs.pop();
				else if (arg.startsWith("-D")) {
					if (arg == null || arg.length() < 3) {
						warning.append("System properties must be set as -D<systemproperty>=<propertyvalue> e.g. -Djava.home=/usr/home/me\n");
					} else {
						StringTokenizer tokenizer = new StringTokenizer(arg.substring(2),"=");
						if (tokenizer.countTokens() != 2) 
							warning.append("System properties must be set as -D<systemproperty>=<propertyvalue> e.g. -Djava.home=/usr/home/me\n");
						else {
							String property = tokenizer.nextToken();
							String value = tokenizer.nextToken();
							systemProps.setProperty(property,value);
							newProperties.setProperty(property,value);
						}
					}
				} else {
					argVector.add(arg);
				}
			} catch (EmptyStackException e) { break; }
		}
		
		String[] arguments = argVector.toArray(new String[0]);
		
		
		// Check R_HOME is set
		String rHomeString = findProperty(R_HOME);
		if (rHomeString == null) {
			warning.append(R_HOME + " must be set to the path of the R installation\n");
		} else {
			File rhome = new File(rHomeString);
			if (!rhome.exists()) {
				warning.append(R_HOME + " environment must be set to a valid directory; currently set to '" + rHomeString + "'\n");
			}
		}
		if (verbose) System.out.println("R_HOME: " + rHomeString);
		
		// Define R_SHARE_DIR, R_INCLUDE_DIR,R_DOC_DIR
		String rShare = filePath(new String[] {rHomeString,"share"});
		systemProps.setProperty(R_SHARE, rShare);
		if (verbose) System.out.println(R_SHARE + ": " + rShare);
		String rInclude = filePath(new String[]{rHomeString, "include"});
		systemProps.setProperty(R_INCLUDE_DIR, rInclude);
		if (verbose) System.out.println(R_INCLUDE_DIR + ": " + rInclude);
		String rDoc = filePath(new String[]{rHomeString, "doc"});
		systemProps.setProperty(R_DOC_DIR, rDoc);
		if (verbose) System.out.println(R_DOC_DIR + ": " + rDoc);
		
		// Define R_DEFAULT_PACKAGES
		StringBuilder packages = new StringBuilder();
		for (String packagename : DEFAULT_PACKAGES) {
			packages.append(packagename + ", ");
		}
		String rPackages = "";
		if (packages.length() > 0) rPackages = packages.substring(0, packages.length() - 1); 
		systemProps.setProperty(R_DEFAULT_PACKAGES, rPackages);
		if (verbose) System.out.println(R_DEFAULT_PACKAGES + ": " + rPackages);

		// Set JRI_LD_PATH
		String jriPath = "";
		try {
			jriPath = locateFile("jri", rHomeString);
		} catch (FileNotFoundException e) {
			warning.append("Unable to locate jri directory: " + e.getMessage() + "\n");
		} catch (IOException e) {
			warning.append(e.getClass().getName() + " while trying to locate jri directory: " + e.getMessage() + "\n");
		}
		String jriPaths = mergePaths(jriPath, filePath(rHomeString,"bin"), filePath(rHomeString,"lib"));
		String ldPath = findProperty(LD_LIBRARY_PATH);
		// TODO Ensure jriPath is only included in the path once
		String fullLDPath = (ldPath == null || ldPath.length()==0) ? jriPaths : mergePaths(jriPaths,ldPath);
		systemProps.setProperty(LD_LIBRARY_PATH,	fullLDPath);
		if (verbose) System.out.println(LD_LIBRARY_PATH + ": " + fullLDPath);
		
		systemProps.setProperty(LC_NUMERIC, "C");
		if (verbose) System.out.println(LC_NUMERIC + ": " + findProperty(LC_NUMERIC));
		systemProps.setProperty(NO_SIG,"1");
		if (verbose) System.out.println(NO_SIG + ": " + findProperty(NO_SIG));
		
		String rLibrary = "";
		String rJava = "";
		String remoteRServer = "";
		try {
			rLibrary = locateFile("library",rHomeString);
			rJava = locateFile("rJava",rLibrary);
			remoteRServer = locateFile("RemoteREngine",rLibrary);
		} catch (IOException e) {
			warning.append(e.getClass().getName() + " looking for rJava or RemoteREngine installation directories\n");
		}
		String rJavaClasspath = buildRJavaClassPath( rJava, false);
		String classpath = buildClasspath(rHomeString, rJava, useJarFiles, buildDirPath, false);
		String codebase = buildClasspath(rHomeString, rJava, useJarFiles, buildDirPath, true);
		
		if (verbose) System.out.println("Classpath: " + classpath);
		if (verbose) System.out.println("RMI Codebase: " + codebase);

		String policyFile = "";
		try {
			policyFile = locateFile("server.policy", remoteRServer);
		} catch (IOException e) {
			warning.append(e.getClass().getName() + " while locating security policy file\n");
		}
		if (verbose) System.out.println("Policy file: " + policyFile);

		if (warning.length() > 0) {
			System.err.println("Environment set up failed:\n");
			System.err.println(warning.toString());
			return new Vector<String>(0);
		} 

		// Environment is checked and passed
		System.setProperties(systemProps);
		Vector<String> command = new Vector<String>();
		command.add(javaCmd);
		command.add("-X" + memorySize);
		command.add("-classpath");
		command.add(classpath);
		command.add("-Djava.library.path=" + findProperty(LD_LIBRARY_PATH));
		command.add("-Drjava.class.path=" + rJavaClasspath);
		command.add("-Djava.rmi.server.codebase=" + codebase);
		command.add("-Djava.security.policy=" + policyFile);
		if (newProperties.size() > 0) {
			for (Object newProperty : newProperties.keySet()) {
				command.add("-D" + newProperty + "=" + newProperties.getProperty((String)newProperty));
			}
		}
		command.add(startClass);
		if (arguments.length > 0) {
			for (String arg : arguments) command.add(arg);
		}
		return command;
	}
	
	/**
	 * Method to search along JAVA_HOME to locate the executable for java
	 * @return Fully qualified path to java
	 */
	private String findJava() {
		// Check JAVA_HOME
		String javaHomeString = findProperty(JAVA_HOME);
		if (javaHomeString == null) {
			warning.append(JAVA_HOME + " must be set to the path of the Java installation\n");
		} else {
			File javahome = new File(javaHomeString);
			if (!javahome.exists()) {
				warning.append(JAVA_HOME + " environment must be set to a valid directory; currently set to '" + javaHomeString + "'\n");
			}
		}
		if (verbose) System.out.println(JAVA_HOME + ": " + javaHomeString);

		String javaCmd = "";
		try {
			javaCmd = locateJava(javaHomeString);
		} catch (IOException e) {
			warning.append(e.getClass().getName() + " while locating Java command relative to '" + javaHomeString + "'\n");
		}
			
		if (javaCmd.length() == 0) {
			warning.append("Unable to locate java executable, searching below '" + javaHomeString + "'\n");
		}
		systemProps.setProperty(JAVACMD, javaCmd);
		if (verbose) System.out.println(JAVACMD + ": " + javaCmd);
		
		return javaCmd;
	}
	
	/**
	 * Helper method to build the path to the rJava java files
	 * @param rJava Path to the rJava installation
	 * @param convertToCodebase Flag to determine if path should be converted to RMI codebase syntax
	 * @return classpath components for rJava
	 */
	private String buildRJavaClassPath(String rJava, boolean convertToCodebase) {
		try {
			String rJavaBoot = (convertToCodebase ? "file:" : "") + filePath(new String[] {rJava,"java","boot"},true);
			String rJavaPath = (convertToCodebase ? "file:" : "") + filePath(new String[] {rJava,"java"},true);
			String rJavaClasspath = mergePaths(rJavaBoot,rJavaPath);
			if (convertToCodebase) {
				rJavaClasspath = rJavaClasspath.replace("\\", "/");
				rJavaClasspath = rJavaClasspath.replace(";", ":");
			}
			return rJavaClasspath;
		} catch (FileNotFoundException e) {
			warning.append("Unable to locate rJava classpath directories\n");
		}
		return "";
	}
	
	/**
	 * Construct the classpath for the java launch
	 * @param remoteRServer Path to the installation directory for RemoteREngine
	 * @param useJarFiles Flag to indicate whether the server jar file or class files should be referenced
	 * @param buildDirPath Path to the root of the java class files
	 * @param convertToCodebase Flag to determine whether classpath should be converted to RMI codebase syntax
	 * @return full path
	 */
	private String buildClasspath(String remoteRServer, String rJava, boolean useJarFiles, String buildDirPath, boolean convertToCodebase) {
		Vector<String> classpath = new Vector<String>();
		
		try {
			if (useJarFiles) {
				classpath.add((convertToCodebase ? "file:" : "") + locateFile(SERVER_JARFILE, remoteRServer));
			} else {
				classpath.add((convertToCodebase ? "file:" : "") + locateFile("annotations",buildDirPath) + "\\");
				classpath.add((convertToCodebase ? "file:" : "") + locateFile("client",buildDirPath) + "\\");
				classpath.add((convertToCodebase ? "file:" : "") + locateFile("common",buildDirPath) + "\\");
				classpath.add((convertToCodebase ? "file:" : "") + locateFile("server",buildDirPath) + "\\");
				classpath.add((convertToCodebase ? "file:" : "") + locateFile("stubs",buildDirPath) + "\\");
				classpath.add((convertToCodebase ? "file:" : "") + buildDirPath + "\\");
			}
		} catch (IOException e) {
			warning.append(e.getClass().getName() + " while building classpath\n");
		}
		
		// We don't need to include the rJava path within the RMI Codebase
		if (!convertToCodebase) classpath.add(buildRJavaClassPath(rJava, convertToCodebase));
		String classpathStr = mergePaths(classpath.toArray(new String[0]));
		if (convertToCodebase) {
			classpathStr = classpathStr.replace("\\", "/");
			classpathStr = classpathStr.replace(";", ":");
		}
		return classpathStr;
	}

	/**
	 * Search for the Java executable within the supplied list of
	 * path locations
	 * @param javaHomeString Starting point for all the searches, the Java Home Directory
	 * @return String containing the path or an empty String
	 * @throws IOException Error checking directory location
	 */
	private String locateJava(String javaHomeString) throws IOException {
		String javaCmd = findProperty(JAVACMD);
		if (javaCmd.equals("")) {
			// IBM's JDK on AIX uses strange locations for the executables
			Vector<String[]> searchLocations = new Vector<String[]>(7);
			searchLocations.add(new String[] {javaHomeString, "jre", "sh"});
			searchLocations.add(new String[] {javaHomeString, "jre", "bin"});
			searchLocations.add(new String[] {javaHomeString, "bin"});
			searchLocations.add(new String[] {javaHomeString, "jre", "bin"});
			searchLocations.add(new String[] {javaHomeString, "bin"});
			searchLocations.add(new String[] {javaHomeString, "jre", "bin"});
			
			String[] fileMatch = new String[] {"java","java.exe"};
			for (String[] searchPath : searchLocations) {
				try {
					javaCmd = locateFile(fileMatch, searchPath);
				} catch (FileNotFoundException e) {
					// Don't need to do anything 
				}
				// have located it
				if (javaCmd.length() > 0) break;
			}
		}
		return javaCmd;
	}
	
	/**
	 * Method to locate a file or directory starting from a known path on the system
	 * @param target End of the filename or directory name to be searched for
	 * @param start Path to the directory to start checking from
	 * @return Fully qualified path to the file or directory if found
	 * @throws FileNotFoundException Unable to locate file or supplied information was null
	 * @throws IOException Error interpreting path information
	 */
	private String locateFile(String target, String start) throws FileNotFoundException, IOException {
		return locateFile(new String[]{target}, new String[]{start});
	}
	
	/**
	 * Method to locate a file or directory starting from a known path on the system
	 * @param targets End of the filename or directory name to be searched for
	 * @param start Path to the directory to start checking from
	 * @return Fully qualified path to the file or directory if found
	 * @throws FileNotFoundException Unable to locate file or supplied information was null
	 * @throws IOException Error interpreting path information
	 */
	private String locateFile(String[] targets, String[] start) throws FileNotFoundException, IOException {
		if (targets == null || targets.length == 0) throw new FileNotFoundException("Target not defined");
		if (start == null || start.length == 0) throw new FileNotFoundException("Start not defined");
		Stack<File>directoriesToCheck = new Stack<File>();
		File startingPoint = new File(filePath(start));
		directoriesToCheck.push(startingPoint);
		if (startingPoint == null || !startingPoint.exists()) throw new FileNotFoundException("Unable to find '" + startingPoint +"'");
		boolean found = false;
		while (directoriesToCheck.size() > 0 && !found) {
			File currentDir = directoriesToCheck.pop();
			File[] dirFiles = currentDir.listFiles();
			for (File file : dirFiles) {
				for (String target : targets) {
					if (file.getAbsolutePath().endsWith(target)) {
						return file.getAbsolutePath();
					}
				}
				if (file.isDirectory()) directoriesToCheck.push(file);
			}
		}
		throw new FileNotFoundException("Unable to locate '" + targets + "' beneath '" + start + "'");
	}
	
	/**
	 * Method to combine a series of paths together into a single string using path separator 
	 * character for the current operating system. This method will return an empty string if 
	 * insufficient information is passed into it.
	 * @param paths Paths to be combined together
	 * @return Combined path
	 */
	private String mergePaths(String... paths) {
		return mergePaths(paths,findProperty("path.separator"));
	}

	/**
	 * Method to combine a series of paths together into a single string using the supplied
	 * path separator character. This method will return an empty string if insufficient
	 * information is passed into it.
	 * @param paths Paths to be combined together
	 * @param pathSep
	 * @return Combined path
	 */
	private String mergePaths(String[] paths, String pathSep) {
		if (paths == null || pathSep == null) return "";
		StringBuilder fullPath = new StringBuilder();
		for (String path : paths) {
			if (path != null && path.length() > 0) {
				fullPath.append(path);
				fullPath.append(pathSep);
			}
		}
		
		return (fullPath.length() > 0) ? fullPath.substring(0, fullPath.length() -1) : "";
	}
	
	/**
	 * Concatenate a series of path elements together
	 * @param elements path elements to be combined together
	 * @return Single path element containing all the entries supplied
	 */
	private String filePath(String... elements) {
		try {
			return filePath(elements, findProperty("file.separator"),false);
		} catch (FileNotFoundException e) {
			// we should never get here
			throw new RuntimeException("Unknown error within filePath",e);
		}
	}
	
	/**
	 * Combine a series of sub-directory locations together to form a path
	 * using the file separator supplied
	 * @param elements Sub-directories to be joined together
	 * @param check Flag to indicate whether to check if the resultant path exists
	 * @return Path string Combined path
	 * @throws FileNotFoundException If checking and failed to find the path
	 */
	private String filePath(String[] elements, boolean check) throws FileNotFoundException {
		return filePath(elements, findProperty("file.separator"),check);
	}
	
	/**
	 * Combine a series of sub-directory locations together to form a path
	 * using the file separator supplied
	 * @param elements Sub-directories to be joined together
	 * @param sep Character to be used as the file separator
	 * @param check Flag to indicate whether to check if the resultant path exists
	 * @return Path string Combined path
	 * @throws FileNotFoundException If checking and failed to find the path
	 */
	private String filePath(String[] elements, String sep, boolean check) throws FileNotFoundException {
		StringBuilder path = new StringBuilder();
		for (String element : elements) {
			path.append(element);
			path.append(sep);
		}
		String fullPath = (path.length() > 0)? path.substring(0, path.length()-1) : "";
		if (check) {
			File file = new File(fullPath);
			if (!file.exists()) throw new FileNotFoundException(fullPath + " does not exist");
		}
		return fullPath;
	}
	
	private String findProperty(String propertyName) {
		if (systemProps.containsKey(propertyName)) return systemProps.getProperty(propertyName);

		Map<String, String> environment = System.getenv();
/*		if (verbose) {
			for (String key : environment.keySet()) 
				System.out.println(key + ": " + environment.get(key));
		}
*/
		if (environment.containsKey(propertyName)) return environment.get(propertyName);
		if (verbose) System.out.println("Unable to find property for '" + propertyName + "'");
		return "";
	}

	/**
	 * Start the program
	 * @param args Command line arguments @see #buildLaunchCommand()
	 */
	public static void main(String[] args) {
		Launcher launcher = new Launcher(args);
		String javaCmd = launcher.findJava();
		Vector<String> launchCommand = launcher.buildLaunchCommand(javaCmd);
		if (launcher.verbose) {
			System.out.println("\nCommand: ");
			for (String component : launchCommand) {
				System.out.println(component);
			}
		}

		if (launcher.isTestMode() ) {
			if (!launcher.verbose) {// verbose mode already writes the command out
				System.out.println("\nCommand: ");
				StringBuilder builder = new StringBuilder();
				for (String component : launchCommand) {
					System.out.println(component);
					builder.append(component + " ");
				}
				File launchfile = null;
				BufferedWriter bw = null;
				try {
					launchfile = new File("launchcommand.txt");
					bw = new BufferedWriter(new FileWriter(launchfile));
					bw.write(builder.toString() + "\n");
				} catch (IOException e) {
					System.err.println(e.getClass().getName() + " writing command to " + launchfile.getAbsolutePath() +
							": " + e.getMessage());
				} finally {
					try {
						bw.flush();
						bw.close();
					} catch (Exception e) {}
				}
			}	
		} else {
			System.out.println("Launcher classpath: " + System.getProperty("java.class.path"));
			Runtime runtime = Runtime.getRuntime();
			ProcessStreamHandler stdOutHandler = null;
			ProcessStreamHandler stdErrHandler = null;
			InputHandler stdInHandler = null;
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in) );

				Process process = runtime.exec(launchCommand.toArray(new String[0]));
				stdOutHandler = new ProcessStreamHandler(process.getInputStream());
				stdErrHandler = new ProcessStreamHandler(process.getErrorStream());
				stdInHandler = new InputHandler(process.getOutputStream());
				OutputStream os = process.getOutputStream();
				
				Thread stdOutThread = new Thread(stdOutHandler,"stdOutHandler");
				Thread stdErrThread = new Thread(stdErrHandler,"stdErrHandler");
				Thread stdInThread = new Thread(stdInHandler,"stdInHandler");
				
				stdOutThread.start();
				stdErrThread.start();
				stdInThread.start();
				
	            int exitVal = process.waitFor();
	            System.out.println("ExitValue: " + exitVal);
				
			} catch (Throwable e) {
				System.err.println(e.getClass().getName() + " while running server; " + e.getMessage());
			} finally {
				if (stdOutHandler != null) stdOutHandler.close();
	            if (stdErrHandler != null) stdErrHandler.close();
	            if (stdInHandler != null) stdInHandler.close();
			}
		}
	}
	
	/**
	 * Public constructor
	 * @param args Command line arguments @see #buildLaunchCommand()
	 */
	public Launcher(String[] args) {
		this.args=args;
	}

	/**
	 * Return whether or not this launcher is running in test mode - within test mode it only
	 * prints out the command to run the process rather than try and run it.
	 * @return true if running within test mode.
	 */
	public boolean isTestMode() {
		return testMode;
	}
}
