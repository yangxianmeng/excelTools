package excel;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Predicate;

import log.AbstractLog;

/**
 * @author Yxm
 **/
public class ExcelUtils {

    public static boolean mkdirs(String path, AbstractLog log) {
        File dir = new File(path);
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                log.error(String.format("生成文件的路径设置错误：%s存在但不是一个目录", path));
                return false;
            }
        } else if (!dir.mkdirs()) {
            log.error(String.format("生成文件的路径设置错误：创建目录%s失败", path));
            return false;
        }
        return true;
    }


    public static void copyFiles(String source, String target, Predicate<File> predicate, boolean overwrite) throws Exception {
        File sourceFile = new File(source);
        if (!sourceFile.exists()) {
            throw new Exception("原目录不存在：" + source);
        } else if (!sourceFile.isDirectory()) {
            throw new Exception("源目录存在，但不是一个目录：" + source);
        } else {
            File targetFile = new File(target);
            if (targetFile.exists()) {
                if (!targetFile.isDirectory()) {
                    throw new Exception("目标目录存在，但不是一个目录：" + target);
                }
            } else if (!targetFile.mkdirs()) {
                throw new Exception("创建目标目录失败：" + target);
            }
            CopyOption[] options = overwrite ? new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} : new CopyOption[0];
            Path sourcePath = sourceFile.toPath();
            Path targetPath = targetFile.toPath();
            File[] var9 = sourceFile.listFiles(predicate::test);
            assert var9 != null;
            for (File file : var9) {
                Files.copy(file.toPath(), targetPath.resolve(sourcePath.relativize(file.toPath())), options);
            }

        }
    }

    public static void deleteFiles(String start, Predicate<File> predicate) throws Exception {
        File startFile = new File(start);
        if (startFile.exists()) {
            if (!startFile.isDirectory()) {
                throw new Exception("目录存在，但不是一个目录：" + start);
            } else {
                File[] var3 = startFile.listFiles(predicate::test);
                assert var3 != null;
                for (File file : var3) {
                    file.delete();
                }
            }
        }
    }

    public static void copyFileTree(String source, String target) throws IOException {
        final Path sourcePath = Paths.get(source);
        final Path targetPath = Paths.get(target);
        Files.walkFileTree(sourcePath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new SimpleFileVisitor<>() {
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path target = targetPath.resolve(sourcePath.relativize(dir));
                try {
                    Files.copy(dir, target);
                } catch (FileAlreadyExistsException var5) {
                    if (!Files.isDirectory(target)) {
                        throw var5;
                    }
                }
                return FileVisitResult.CONTINUE;
            }
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, targetPath.resolve(sourcePath.relativize(file)));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void deleteFileTree(String start) throws IOException {
        Path startPath = Paths.get(start);
        Files.walkFileTree(startPath, new SimpleFileVisitor<>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                if (e == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    throw e;
                }
            }
        });
    }

    public static ArrayList<File> getFileList(File file) {
        //获取所有excel文件
        ArrayList<File> listfiles = new ArrayList<>();
        if (file.isFile() && file.getName().matches("^[A-Za-z0-9_$]+\\.xls(?:x|m|)")) {
            listfiles.add(file);
        } else if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                if (f.isDirectory()) {
                    listfiles.addAll(getFileList(f));
                } else if (f.isFile() && f.getName().matches("^[A-Za-z0-9_$]+\\.xls(?:x|m|)")) {
                    listfiles.add(f);
                }
            }
        }
        return listfiles;
    }
}
