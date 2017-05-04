package control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class FileUtil {

    private static final Logger LOG = Logger.getLogger(FileUtil.class);

    public static void saveList(List<?> list, File file) {
	LOG.info("Saving list to file: " + file.getName());
	try {
	    FileOutputStream fos = new FileOutputStream(file);
	    ObjectOutputStream oos = new ObjectOutputStream(fos);
	    oos.writeObject(list);
	    oos.close();
	} catch (Exception e) {
	    LOG.error("Error while writing list", e);
	}
    }

    public static List<?> loadList(File file) {
	ArrayList<?> result = new ArrayList<>();
	LOG.info("Loading list from file: " + file.getName());
	if (!file.exists() || file.isDirectory()) {
	    LOG.warn("Bulb List File not found");
	} else {
	    try {
		FileInputStream fos = new FileInputStream(file);
		ObjectInputStream oos = new ObjectInputStream(fos);
		result = (ArrayList<?>) oos.readObject();
		LOG.info("Loaded " + result.size() + " objects from list");
		oos.close();
	    } catch (Exception e) {
		LOG.error("Error while reading list from file " + file.getName(), e);
	    }
	}
	return result;
    }
}
