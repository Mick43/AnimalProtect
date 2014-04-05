package de.AnimalProtectOld.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import de.AnimalProtectOld.Main;

public class CrashfileObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static transient int counter = 0;

	private transient File filename;
	private Serializable object;

	public CrashfileObject(Main plugin, String filename) {
		this(plugin, null, filename);
		this.deserialize();
	}

	public CrashfileObject(Main plugin, Serializable object) {
		this(plugin, object, generateFilename());
		this.serialize();
	}

	public CrashfileObject(Main plugin, Serializable object, String filename) {
		this.object = object;
		this.filename = new File(plugin.getDataFolder() + File.separator
				+ "crashFiles" + File.separator + filename);
		this.serialize();
	}

	private static String generateFilename() {
		return "crash_" + System.currentTimeMillis() + "_" + (++counter)
				+ ".dat";
	}

	public void serialize() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(filename));
			oos.writeObject(this.object);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deserialize() {
		try {
			if (filename.exists()) {
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(filename));
				this.object = (Serializable) ois.readObject();
				ois.close();
			} else {
				throw new FileNotFoundException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getObject() {
		return object;
	}

}
