package org.gotti.wurmunlimited.patcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class PatchServerJar {
	
	private enum Os {
		Windows {

			@Override
			public String getPatchedBinaryName() {
				return "WurmServerLauncher-patched.exe";
			}

			@Override
			public String getOriginalBinaryName() {
				return "WurmServerLauncher.exe";
			}
			
		},
		Linux {

			@Override
			public String getPatchedBinaryName() {
				return "WurmServerLauncher-patched";
			}

			@Override
			public String getOriginalBinaryName() {
				return "WurmServerLauncher";
			}
			
		};
		
		public static Os getOs() {
			if (isWindows()) {
				return Os.Windows;
			} else {
				return Os.Linux;
			}
			
		}
		
		public static boolean isWindows() {
			return System.getProperty("os.name").startsWith("Windows");
		}
		
		public abstract String getPatchedBinaryName();
		public abstract String getOriginalBinaryName();
		
	}

	private static Logger logger = Logger.getLogger(PatchServerJar.class.getName());


	private void run() throws NotFoundException, CannotCompileException, IOException {

		Path serverJar = Paths.get("server.jar");
		Path loaderJar = Paths.get("modlauncher.jar");
		try (FileSystem serverFS = FileSystems.newFileSystem(URI.create("jar:" + serverJar.toUri()), new HashMap<>()); FileSystem loaderFS = FileSystems.newFileSystem(URI.create("jar:" + loaderJar.toUri()), new HashMap<>())) {
			ClassPool classPool = ClassPool.getDefault();

			Path origFile = serverFS.getPath("PatchedLauncher.class");
			if (Files.exists(origFile)) {
				logger.info("PatchedLauncher does already exist. server.jar is already patched");
				return;
			}

			Path loaderFile = loaderFS.getPath("org/gotti/wurmunlimited/serverlauncher/PatchedLauncher.class");
			if (!Files.exists(loaderFile)) {
				throw new FileNotFoundException(loaderFile.toString());
			}

			CtClass loaderClass;
			try (InputStream inputStream = Files.newInputStream(loaderFile)) {
				loaderClass = classPool.makeClass(inputStream);
			}
			loaderClass.setName("PatchedLauncher");

			try (OutputStream outputStream = Files.newOutputStream(origFile, StandardOpenOption.CREATE)) {
				loaderClass.toBytecode(new DataOutputStream(outputStream));
			}
		}

		logger.info("Added loader to server.jar");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Os os = Os.getOs();
		Files.copy(Paths.get(os.getOriginalBinaryName()), baos);

		byte[] search = "com/wurmonline/server/gui/WurmServerGuiMain\0".getBytes(StandardCharsets.UTF_8);
		byte[] replacement = "PatchedLauncher\0".getBytes(StandardCharsets.UTF_8);
		if (replacement.length > search.length) {
			throw new RuntimeException("Replacement is larger than source");
		}

		byte[] exeData = baos.toByteArray();

		int pos = findCode(exeData, search);
		Arrays.fill(exeData, pos, pos + search.length, (byte) 0);
		System.arraycopy(replacement, 0, exeData, pos, replacement.length);

		Files.copy(new ByteArrayInputStream(exeData), Paths.get(os.getPatchedBinaryName()), StandardCopyOption.REPLACE_EXISTING);

		logger.info("Patched " + os.getPatchedBinaryName());
	}

	// Find the code fragment
	private int findCode(byte[] code, byte[] search) {
		for (int i = 0, j = 0, backtrack = 0; i < code.length && j < search.length; i++) {
			if (code[i] == search[j]) {
				if (j == 0) {
					backtrack = i;
				}
				j++;
				if (j == search.length) {
					return backtrack;
				}
			} else if (j > 0) {
				i = backtrack;
				j = 0;
			}
		}
		throw new RuntimeException("Classname not found");
	}

	public static void main(String[] args) {

		try {
			new PatchServerJar().run();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
