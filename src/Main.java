import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static final String ROOT_PATH = "Games";
    static StringBuilder log = new StringBuilder();

    public static void main(String[] args) throws InterruptedException {
        File src = makeDir(ROOT_PATH, "src");
        File res = makeDir(ROOT_PATH, "res");
        File savegames = makeDir(ROOT_PATH, "savegames");
        File temp = makeDir(ROOT_PATH, "temp");
        //В каталоге src создайте две директории: main, test
        File main = makeDir(src, "main");
        File test = makeDir(src, "test");
        //В подкаталоге main создайте два файла: Main.java, Utils.java
        File f1 = makeFile(main, "Main.java");
        File f2 = makeFile(main, "Utils.java");
        //В каталоге res создайте три директории: drawables, vectors, icons
        File draw = makeDir(res, "drawables");
        File vectors = makeDir(res, "vectors");
        File icons = makeDir(res, "icons");
        //В директории temp создайте файл temp.txt
        File f3 = makeFile(temp, "temp.txt");
        //запись лога в файл temp.txt
        GameProgress gm1 = new GameProgress(100, 200, 300, 35.9);
        GameProgress gm2 = new GameProgress(105, 205, 305, 40.5);
        GameProgress gm3 = new GameProgress(205, 205, 405, 50.5);
        saveGame("Games//savegames//save1.dat", gm1);
        saveGame("Games//savegames//save2.dat", gm2);
        saveGame("Games//savegames//save3.dat", gm3);
        zipFiles("Games//savegames//save.zip", new String[]{"Games//savegames//save1.dat", "Games//savegames//save2.dat", "Games//savegames//save3.dat"});
        deleteFiles(savegames);
        try (FileWriter out = new FileWriter(f3)) {
            out.write(log.toString());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        File unzip = makeDir(savegames, "unzip");
        openZip("Games//savegames//save.zip", unzip.getPath());
        GameProgress gp = openProgress(unzip.listFiles()[0].getPath());
        System.out.println(gp.toString());
    }

    static File makeDir(String parent, String path) {
        File file = new File(parent, path);
        String logStr;
        if (file.mkdir()) {
            logStr = String.format("Directory %s has been successfully created.\n", file.getName());
        } else {
            logStr = String.format("Directory %s has not been created.\n", file.getName());
        }
        log.append(logStr);
        return file;
    }

    static File makeDir(File parent, String path) {
        File file = new File(parent, path);
        String logStr;
        if (file.mkdirs()) {
            logStr = String.format("Directory %s has been successfully created.\n", file.getName());
        } else {
            logStr = String.format("Directory %s has not been created.\n", file.getName());
        }
        log.append(logStr);
        return file;
    }

    static File makeFile(File parent, String path) {
        File file = new File(parent, path);
        String logStr;
        try {
            file.createNewFile();
            logStr = String.format("File %s has been successfully created.\n", file.getName());
            log.append(logStr);
        } catch (IOException ex) {
            logStr = String.format("File %s has not been created: ", file.getName());
            log.append(logStr);
            log.append(ex.getMessage());
            log.append("\n");
        }
        return file;
    }

    //для удаления всего дерева
    static void recursiveDeletion(File file) {
        File[] contents = file.listFiles();
        if (contents != null || !file.isFile()) {
            for (File f : contents) {
                recursiveDeletion(f);
            }
        }
        if (!file.getName().equals(ROOT_PATH))
            file.delete();
    }

    static void deleteFiles(File file) {
        File[] contents = file.listFiles();
        if (contents != null || !file.isFile()) {
            for (File f : contents) {
                deleteFiles(f);
            }
        }
        if (!file.getName().substring(file.getName().length() - 3).equals("zip") && file.isFile()) {
            file.delete();
            log.append(String.format("File %s has been deleted\n", file.getName()));
        }
    }

    static void saveGame(String path, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    static void zipFiles(String path, String[] list) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(path))) {
            for (String item : list) {
                try (FileInputStream fis = new FileInputStream(item)) {
                    ZipEntry entry1 = new ZipEntry(item);
                    zout.putNextEntry(entry1);
                    byte[] buff = new byte[fis.available()];
                    fis.read(buff);
                    zout.write(buff);
                    zout.closeEntry();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void openZip(String zipPath, String targetPath) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry1;
            String name;
            String newName;
            while ((entry1 = zin.getNextEntry()) != null) {
                name = entry1.getName();
                newName = "new" + name.substring(name.lastIndexOf("//") + 2);
                FileOutputStream fout = new FileOutputStream(new File(targetPath, newName));
                for (int i = zin.read(); i != -1; i = zin.read()) {
                    fout.write(i);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static GameProgress openProgress(String pathToGame) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathToGame))) {
            GameProgress gp = (GameProgress) ois.readObject();
            return gp;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
