package Control;


import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FileHandler
{
	public static void save(Serializable ser, String path, boolean overwrite)
	{
		try
		{
			if (new File(path).exists() && !overwrite)
				path += "("+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy-MM-dd-HH-mm-ss"))+")";
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.close();
			System.out.println("Save to: "+path+" successful.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println("Save to: "+path+" fail.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T open(T t, String path)
	{
		try
		{
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Serializable ser = (Serializable) ois.readObject();
			ois.close();
			return t.getClass().isInstance(ser) ? (T) ser : t;
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
			System.err.println("Open from: "+path+" fail.");
			return t;
		}
	}
}