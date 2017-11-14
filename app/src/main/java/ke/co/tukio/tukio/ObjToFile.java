package ke.co.tukio.tukio;

    import android.os.Environment;

    import java.io.*;

    public class ObjToFile {

        public static String ObjToFile(Object object) throws IOException {
            String path = Environment.getExternalStorageDirectory() + File.separator + "cache" + File.separator;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            path += "data";
            File data = new File(path);
            if (!data.createNewFile()) {
                data.delete();
                data.createNewFile();
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(data));
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            return path;
        }

        public static Object objectFromFile(String path) throws IOException, ClassNotFoundException {
            Object object = null;
            File data = new File(path);
            if (data.exists()) {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(data));
                object = objectInputStream.readObject();
                objectInputStream.close();
            }
            return object;
            }
}