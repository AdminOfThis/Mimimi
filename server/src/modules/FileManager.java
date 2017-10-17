package modules;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;

public class FileManager {

	public enum FileCategory {
		ROOT, TEMP, AUDIO;

		@Override
		public String toString() {
			switch (this) {
			case ROOT:
				return DATA_HOME + "/..";
			case AUDIO:
				return DATA_HOME + "/audio";
			case TEMP:
				return DATA_HOME + "/temp";
			}
			return DATA_HOME + "/etc";
		}
	};

	private static final Logger	LOG			= Logger.getLogger(FileManager.class);
	private static final String	DATA_HOME	= "./data";
	private static FileManager	instance;
	private File				dataRoot;

	private FileManager() {
		dataRoot = new File(DATA_HOME);
		if (!dataRoot.exists()) {
			LOG.info("Data directory not found, creating new Folder");
			dataRoot.mkdirs();
		}
	}

	public static FileManager getInstance() {
		if (instance == null) {
			instance = new FileManager();
		}
		return instance;
	}

	public boolean addFile(FileCategory cat, File file) {
		if (!file.exists()) {
			LOG.warn("File does not exist");
			return false;
		}
		File destinationFolder = new File(cat.toString());
		if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
			LOG.info("Destination folder does not exist, mkdirs");
			destinationFolder.mkdirs();
		}
		try {
			Path destinationPath = destinationFolder.toPath().resolve(file.getName());
			Files.move(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
			LOG.info("File moved to " + destinationPath.toString());
		} catch (IOException e) {
			LOG.error("Unable to move file", e);
			return false;
		}
		return true;
	}

	public boolean addFile(FileCategory cat, String filename, byte[] data, int len) {
		try {
			File f = new File(cat.toString() + "/" + filename);
			f.createNewFile();
			FileOutputStream out = new FileOutputStream(f, true);
			out.write(data, 0, len);
			out.flush();
			out.close();
			LOG.info("File sucessfully received");
		} catch (Exception e) {
			LOG.error("Unable to receive file", e);
			return false;
		}
		return true;
	}

}
