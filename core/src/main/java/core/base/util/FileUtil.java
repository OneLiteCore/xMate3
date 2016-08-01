package core.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import core.base.Core;
import core.base.file.FileExistsException;

/**
 * 文件系统工具集。
 * file代表文件；
 * dir代表目录；
 * item代表文件或者目录；
 *
 * @author DrkCore
 * @since 2015年9月24日21:04:08
 */
public final class FileUtil {

    private FileUtil() {
    }

	/* 创建项目 */

    /**
     * 创建文件，具体实现请参阅{@link #createFile(File)}
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static File createFile(String filePath) throws IOException {
        return createFile(new File(filePath));
    }

    /**
     * 创建文件。当其上级目录不存在时，将会创建所有上级目录。
     * 当且仅当文件不存在且成功创建文件时返回创建好的文件，否则将抛出异常。
     *
     * @param file
     * @return
     * @throws FileExistsException
     * @throws IOException
     */
    public static File createFile(File file) throws IOException {
        if (file.exists()) {
            throw new FileExistsException(file);
        }

        // 创建父目录
        File parentDir = file.getParentFile();
        if (parentDir != null) {
            parentDir.mkdirs();
        }
        // 创建新文件
        file.createNewFile();
        if (file.isFile()) {
            return file;
        } else {
            throw new IOException("无法创建文件");
        }
    }

    /**
     * 创建目录。具体实现请参阅{@link #createDir(File)}
     *
     * @param dirPath
     * @return
     * @throws IOException
     */
    public static File createDir(String dirPath) throws IOException {
        return createDir(new File(dirPath));
    }

    /**
     * 创建目录。当其上级目录不存在时会创建所有上级目录。
     * 当且仅当dir不存在并且创建成功时返回dir，否则抛出异常。
     *
     * @param dir
     * @return
     * @throws IOException
     */
    public static File createDir(File dir) throws IOException {
        if (dir.exists()) {
            throw new FileExistsException(dir);
        }
        dir.mkdirs();
        if (dir.isDirectory()) {
            return dir;
        } else {
            throw new IOException("无法创建目录");
        }
    }

    /**
     * 获取或创建指定路径下的文件，具体实现请参阅{@link #getOrCreateFile(File)}
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static File getOrCreateFile(String filePath) throws IOException {
        return getOrCreateFile(new File(filePath));
    }

    /**
     * 获取或创建指定路径下的文件。
     * <li/>文件存在时直接返回；
     * <li/>文件不存在时创建并返回，创建的方法参阅
     * {@link #createFile(File)}；
     * <li/>存在同名目录时抛出异常。
     *
     * @param file
     * @return
     * @throws FileExistsException
     * @throws IOException
     */
    public static File getOrCreateFile(File file) throws IOException {
        if (file.isFile()) {
            return file;
        } else if (!file.exists() && createFile(file).isFile()) {// 文件不存在但创建文件成功
            return file;
        } else if (file.isDirectory()) {
            throw new FileExistsException("同名目录已存在");
        } else {// 其他情况，包括指定路径为dir，或者文件不存在但创建失败
            throw new IOException("无法获取或创建文件：" + file);
        }
    }

    /**
     * 获取或创建指定路径下的目录，具体实现请参阅{@link #getOrCreateDir(File)}。
     *
     * @param dirPath
     * @return
     * @throws IOException
     */
    public static File getOrCreateDir(String dirPath) throws IOException {
        return getOrCreateDir(new File(dirPath));
    }

    /**
     * 获取或创建指定路径下的目录。
     * <li/>目录存在时直接返回；
     * <li/>目录不存在时创建并返回，创建方法参阅{@link #createDir(File)}；
     * <li/>存在同名的文件时排除异常。
     *
     * @param dir
     * @return
     * @throws IOException
     */
    public static File getOrCreateDir(File dir) throws IOException {
        if (dir.isDirectory()) {
            return dir;
        } else if (!dir.exists() && createDir(dir).isDirectory()) {// 目录不存在但创建目录成功
            return dir;
        } else if (dir.isFile()) {
            throw new FileExistsException("同名文件已存在");
        } else {// 其他情况
            throw new IOException("无法获取或创建目录：" + dir);
        }
    }

    /**
     * {@link #confirmFile(File)}
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static File confirmFile(String filePath) throws IOException {
        return confirmFile(new File(filePath));
    }

    /**
     * 强制确保路径为文件
     *
     * @param file 需要确保的文件
     * @return 确保为文件的File实例
     * @throws IOException 当文件不存在且无法创建，或者路径指向了目录却无法删除再创建为文件，抛出该异常
     */
    public static File confirmFile(File file) throws IOException {
        if (file.isFile()) {// 是文件
            return file;
        } else if (!file.exists() && createFile(file).isFile()) {// 不存在但是创建成功
            return file;
        } else if (file.isDirectory() && deleteItem(file) && createFile(file).isFile()) {// 是目录，但是删除后创建文件成功
            return file;
        } else {
            throw new IOException("文件不可用");
        }
    }

    /**
     * {@link #confirmDir(File)}
     *
     * @param dirPath
     * @return
     * @throws FileNotFoundException
     */
    public static File confirmDir(String dirPath) throws FileNotFoundException {
        return confirmDir(new File(dirPath));
    }

    /**
     * 强制确保路径为目录
     *
     * @param dir 需要确保的目录
     * @return 确认为目录的File实例
     * @throws FileNotFoundException 当dir不存在且无法创建，或者是文件却无法删除后再创建为文件，抛出改异常
     */
    public static File confirmDir(File dir) throws FileNotFoundException {
        if (dir.isDirectory()) {// 是目录
            return dir;
        } else if (!dir.exists() && dir.mkdirs()) {// 不存在但是创建成功
            return dir;
        } else if (dir.isFile() && dir.delete() && dir.mkdirs()) {// 是文件，但是删除后创建目录成功
            return dir;
        } else {
            throw new FileNotFoundException("目录不可用");
        }
    }

    /**
     * 在指定目录中创建.nomedia文件来表明目录之下不存在媒体数据。
     *
     * @param inDir
     * @return
     * @throws IOException
     */
    public static File confirmNoMediaFile(File inDir) throws IOException {
        return confirmFile(new File(inDir, ".nomedia"));
    }

    /**
     * 判断指定目录中是否存在.nomedia文件
     *
     * @param inDir
     * @return
     */
    public static File containNoMediaFile(File inDir) {
        File nomediaFile = new File(inDir, ".nomedia");
        return nomediaFile.exists() ? nomediaFile : null;
    }

    public static File[] toItems(String... paths) {
        int len = paths.length;
        File[] itemArr = new File[len];
        for (int i = 0; i < len; i++) {
            itemArr[i] = new File(paths[i]);
        }
        return itemArr;
    }

    public static List<File> toItems(Collection<String> paths) {
        List<File> files = new ArrayList<>(paths.size());
        for (String path : paths) {
            files.add(new File(path));
        }
        return files;
    }

    public static String[] toPaths(File... items) {
        int len = items.length;
        String[] paths = new String[len];
        for (int i = 0; i < len; i++) {
            paths[i] = items[i].getAbsolutePath();
        }
        return paths;
    }

    public static List<String> toPaths(Collection<File> items) {
        List<String> paths = new ArrayList<>(items.size());
        for (File item : items) {
            paths.add(item.getAbsolutePath());
        }
        return paths;
    }

	/* 项目名和路径处理 */

    /**
     * {@link #getFileExtName(String)}
     *
     * @param file
     * @return
     */
    public static String getFileExtName(File file) {
        return getFileExtName(file.getAbsolutePath());
    }

    /**
     * 获取文件的拓展名。注意，这里是以路径来判断的，如果path指向的是目录只要符合规则一样能返回拓展名。
     * 在使用时请多加判断。
     *
     * @param path 文件的路径。该路径分隔符以当前系统为准，即File.separator
     * @return 小写的文件拓展名的字符串，不包括点号，比如“txt”。<br>
     * 如果不存在符合规则的拓展名，则返回null。
     */
    public static String getFileExtName(String path) {
        // 获取文件名称
        String fileName = getItemName(path);
        // 获取小数点所在位置
        int index = fileName.lastIndexOf('.');
        /*
         * 在win操作系统上不允许比如 “.jpg” 的文件名存在。
		 * 但是在android中，“.jpg” 的文件可以存在。并且会被视为隐藏文件。
		 * 这里将 lastIndexOf('.') = 0 的情况为拓展名不存在
		 * 对于 “.jpg” 来说，文件名即为jpg而拓展名不存在
		 * 而对于 “.fileName.txt” 这种情，因为 lastIndexOf('.')
		 * 的值不为0，所以合法
		 * 奇葩的是，在win和android上，“..txt” 的情况都是合法的，不过此时拓展名视为存在
		 * 因而“lastIndexOf('.') = 0 视为拓展名不存在”依然适用
		 */
        if (index > 0) {
            /*
             * 只在小数点存在，并且位置正确时执行
			 * 无论是在win上还是在android上，将文件重命名为比如
			 * “文件名.”时，会默认去掉小数点，即会变成“文件名”
			 * 也就是说，当小数点存在时，index + 1 绝对不会越界
			 */
            return fileName.substring(index + 1).toLowerCase(Locale.getDefault());
        } else {
            return null;
        }
    }

    /**
     * @param file
     * @return
     * @see #getAbsoluteFileName(String)
     */
    public static String getAbsoluteFileName(File file) {
        return getAbsoluteFileName(file.getAbsolutePath());
    }

    /**
     * 获取去除了拓展名后的文件名。
     *
     * @param path 文件路径。该路径分隔符以当前系统为准，即File.separator
     * @return 去除了拓展名后的名称，比如"C:\fileName.TXT"，将返回“fileName”。
     */
    public static String getAbsoluteFileName(String path) {
        String fileName = getItemName(path);
        String type = getFileExtName(path);
        if (type != null) {
            // 移动拓展名
            // 因为截取的type是不带小数点的，所以要多减去1位
            return fileName.substring(0, fileName.length() - type.length() - 1);
        } else {
            return fileName;
        }
    }

    /**
     * {@link #getItemName(String)}
     *
     * @param item
     * @return
     */
    public static String getItemName(File item) {
        return getItemName(item.getAbsolutePath());
    }

    /**
     * 获取项目的名称。
     * 作用效果相当于File.getName()。
     *
     * @param filePath 项目路径。该路径分隔符以当前系统为准，即File.separator
     * @return 文件的名称<br>
     * （如“C:\ABC\”将返回ABC，“C:\1.txt”返回“1.txt”） <br>
     * 如果项目名称为“/”，即Linux或者unix的根目录，则直接返回"/"
     */
    public static String getItemName(String filePath) {
        //统一分隔符
        filePath = filePath.contains("\\") ? filePath.replace('\\', '/') : filePath;
        if (filePath.equals("/")) {
            return "/";
        }
        if (filePath.endsWith("/")) { // 如果目录路径以"/"结尾，则先去除末尾的"/"
            filePath = filePath.substring(0, filePath.length() - 1);
        }
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    /**
     * 判断项目item是否是dir的下级目录。
     * 如果 item不存在或者dir不为目录，必定返回null。
     *
     * @param dir
     * @param item
     * @return
     */
    public static boolean isItemInDir(File dir, File item) {
        if (item.exists() && dir.isDirectory()) {
            String itemPath = item.getAbsolutePath();
            String dirPath = dir.getAbsolutePath();
            if (!dirPath.endsWith(File.separator)) {// 目录路径不是以文件分隔符结尾的，就添加一个上去
                dirPath += File.separator;
            }
            return itemPath.length() > dirPath.length() && itemPath.startsWith(dirPath);
        }
        return false;
    }

    /**
     * 判断项目item是是dir目录的直属项目。
     * 如果item不存在或者dir不是目录，必定返回false。
     *
     * @param dir
     * @param item
     * @return
     */
    public static boolean isItemInDirDirectly(File dir, File item) {
        if (item.exists() && dir.isDirectory()) {
            String itemPath = item.getAbsolutePath();
            String dirPath = dir.getAbsolutePath();
            if (itemPath.length() > dirPath.length() && itemPath.startsWith(dirPath)) {
                String subPath = itemPath.substring(dirPath.length());
                if (subPath.startsWith(File.separator)) {// 去掉开头可能带有的文件分隔符
                    subPath = subPath.substring(1);
                }
                return !subPath.contains(File.separator);
            }
        }
        return false;
    }

    /**
     * 判断两个File是否指向同一个位置
     *
     * @param dstItem
     * @param srcItem
     * @return
     */
    public static boolean isSameItem(File dstItem, File srcItem) {
        return srcItem.getAbsolutePath().equals(dstItem.getAbsolutePath());
    }

    public static void dumpFile(File item, List<File> fileList) {
        dumpFile(item, fileList, null);
    }

    /**
     * 具体实现请参阅{@link #dumpFile(File, List, FileFilter, boolean)}，默认allowHiddenDir为false。
     *
     * @param item
     * @param fileList
     * @param filter
     */
    public static void dumpFile(File item, List<File> fileList, @Nullable FileFilter filter) {
        dumpFile(item, fileList, filter, false);
    }

    /**
     * 使用递归遍历目录，包括隐目录，之下的所有文件，并填入指定的List之中。
     *
     * @param item     如果item本身是文件并且符合filter的要求，也会被添加到list中
     * @param fileList
     * @param filter
     */
    public static void dumpFile(File item, List<File> fileList, @Nullable FileFilter filter, boolean allowHiddenDir) {
        if (item.isFile()) {
            if (filter == null || filter.accept(item)) {
                fileList.add(item);
            }
        } else if (item.isDirectory() && (!item.isHidden() || allowHiddenDir)) {// 是目录，不是隐藏目录，或者允许隐藏目录
            File[] files = item.listFiles();
            if (files != null) {
                for (File check : files) {
                    dumpFile(check, fileList, filter, allowHiddenDir);
                }
            }
        }
    }

	/* 项目大小 */

    /**
     * 获取文件的大小，或者目录下所有项目的大小。
     *
     * @param item 文件或者目录。<br/>
     *             如果文件不存在则抛出异常。
     * @return 通过{@link File#length()}获取文件的路径，
     * 如果是目录则通过递归循环统计文件大小，长度以bytes计。
     * @throws FileNotFoundException
     */
    public static long countItemSize(File item) throws FileNotFoundException {
        if (item.isFile()) {
            return item.length();
        } else if (item.isDirectory()) {
            long sum = 0;

            // 统计
            File[] files = item.listFiles();
            if (files != null && files.length > 0) {
                for (File check : files) {
                    sum += countItemSize(check);
                }
            }

            return sum;
        } else {
            throw new FileNotFoundException("指定文件不存在");
        }
    }

    /**
     * 获取文件的大小或者目录下所有项目的大小，并格式化为简易的字符。
     *
     * @param item
     * @return
     * @throws FileNotFoundException 文件不存在时抛出该异常
     * @see #getFileExtName(String)
     */
    public static String getFormattedItemSize(File item) throws FileNotFoundException {
        long fileLen = countItemSize(item);
        return formatItemSize(fileLen);
    }

    /**
     * 获取内置存储的总大小。
     *
     * @return 以byte计数的大小
     * @see #getTotalSize(String)
     */
    public static long getInnerStorageTotalSize() {
        File inner = Environment.getExternalStorageDirectory();
        return getTotalSize(inner.getAbsolutePath());
    }

    /**
     * 获取内置存储的剩余空间
     *
     * @return 以byte计数的大小
     * @see #getAvailableSize(String)
     */
    public static long getInnerStorageAvailableSize() {
        File inner = Environment.getExternalStorageDirectory();
        return getAvailableSize(inner.getAbsolutePath());
    }

    /**
     * 获取指定位置的总大小。
     *
     * @param path
     * @return 以byte计的空间大小
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getTotalSize(String path) {
        long blockSize;
        long blockCount;
        StatFs statFs = new StatFs(path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {// 高API
            blockSize = statFs.getBlockSizeLong();
            blockCount = statFs.getBlockCountLong();
        } else {
            blockSize = statFs.getBlockSize();
            blockCount = statFs.getBlockCount();
        }
        return blockSize * blockCount;
    }

    /**
     * 获取指定位置的可用空间
     *
     * @param path
     * @return 以byte计数的大小
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static long getAvailableSize(String path) {
        long blockSize;
        long blockAvailable;
        StatFs statFs = new StatFs(path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {// 高API
            blockSize = statFs.getBlockSizeLong();
            blockAvailable = statFs.getAvailableBlocksLong();
        } else {
            blockSize = statFs.getBlockSize();
            blockAvailable = statFs.getAvailableBlocks();
        }
        return blockSize * blockAvailable;
    }

    /**
     * 通过{@link Formatter#formatFileSize(Context, long)} 方法将bytes数格式化为简易的字符串。
     * 比如1024会被格式为1KB。
     *
     * @param fileLen
     * @return
     */
    public static String formatItemSize(long fileLen) {
        return Formatter.formatFileSize(Core.getInstance().getAppContext(), fileLen);
    }

	/* 获取磁盘路径 */

    /**
     * 使用反射调用{@link StorageManager}中的私有方法"getVolumePaths"来获取当前挂在的路径。
     *
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static String[] getVolumeByReflect() throws NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        StorageManager storageMgr = (StorageManager) Core.getInstance().getAppContext().getSystemService(Context.STORAGE_SERVICE);
        Method method = storageMgr.getClass().getMethod("getVolumePaths");
        return (String[]) method.invoke(storageMgr);
    }

    /**
     * 使用Runtime获取存储路径。如果外置SD卡存在则返回，否者为null。
     *
     * @return
     * @throws IOException
     */
    public static String getExtSDCardPathByRuntime() throws IOException {
        Process process = null;
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec("mount");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()));
            String line;
            int mountPathIndex = 1;
            File extFile = Environment.getExternalStorageDirectory();
            while ((line = reader.readLine()) != null) {
                if ((!line.contains("fat") && !line.contains("fuse") && !line.contains("storage"))
                        || line.contains("secure") || line.contains("asec") || line.contains("firmware")
                        || line.contains("shell") || line.contains("obb") || line.contains("legacy")
                        || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data") || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory() || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile.getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                return mountPath;
            }
        } finally {
            if (process != null) {
                process.destroy();
            }
            IOUtil.close(reader);
        }
        return null;
    }

	/*文件操作*/

    /**
     * 冲突时终止操作。届时可能抛出异常
     */
    public static final int CONFLICT_OPERATION_TERMINATE = 0;
    /**
     * 冲突时使用新的项目名词
     */
    public static final int CONFLICT_OPERATION_APPEND_TAG = 1;
    /**
     * 覆盖冲突项目。如果二者均是目录则合并。
     * 但是无论是文件还是目录都无法覆盖自己。
     */
    public static final int CONFLICT_OPERATION_COVER = 2;
    /**
     * 跳过冲突项目
     */
    public static final int CONFLICT_OPERATION_SKIP = 3;

    @IntDef({
            CONFLICT_OPERATION_TERMINATE,
            CONFLICT_OPERATION_APPEND_TAG,
            CONFLICT_OPERATION_COVER,
            CONFLICT_OPERATION_SKIP
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ConflictOperation {
    }

    /**
     * 用于向文件或者目录后添加尾缀以得到一个空的项目的处理工具
     */
    public abstract static class AbsTagAppender {

        /**
         * {@link #getAvailableFileToken(String)}
         *
         * @param baseFile
         * @return
         */
        public final File getAvailableFileToken(File baseFile) {
            return getAvailableFileToken(baseFile.getAbsolutePath());
        }

        /**
         * 获取可用的文件占位
         *
         * @param basePath
         * @return 一个指向可用的空位置的File对象。
         */
        public final File getAvailableFileToken(String basePath) {
            File availableFile = appendTagToFile(basePath);
            if (!availableFile.exists()) {// 文件不存在，好样的
                return availableFile;
            } else {
                throw new IllegalStateException("appendTag所返回的File对象不允许指向一个已存在的项目");
            }
        }

        /**
         * {@link #getAvailableDirToken(String)}
         *
         * @param baseDir
         * @return
         */
        public final File getAvailableDirToken(File baseDir) {
            return getAvailableDirToken(baseDir.getAbsolutePath());
        }

        /**
         * 获取可用的目录占位
         *
         * @param basePath
         * @return 一个指向可用的空位置的File对象。
         */
        public final File getAvailableDirToken(String basePath) {
            File availableFile = appendTagToDir(basePath);
            if (!availableFile.exists()) {// 文件不存在，好样的
                return availableFile;
            } else {
                throw new IllegalStateException("appendTag所返回的File对象不允许指向一个已存在的项目");
            }
        }

        /**
         * 根据基础的路径不断地拼接tag，直到得到一个不存在的文件的路径位置。
         *
         * @param basePath 文件的基本路径
         * @return 指向空位置的文件路径
         */
        protected abstract File appendTagToFile(String basePath);

        /**
         * @param basePath 文件的基本路径
         * @return
         */
        protected abstract File appendTagToDir(String basePath);
    }

	/* 复制项目 */

    private static File copyFile(File dstDir, File srcFile, @ConflictOperation int operation, AbsTagAppender appender) throws IOException {
        // 获取源文件
        if (!srcFile.isFile()) {
            // 源文件不存在，或者是目录
            throw new FileNotFoundException("源文件不可用");
        }

        // 创建目标目录
        dstDir.mkdirs();
        if (!dstDir.isDirectory()) {
            // 如果目标目录在调用了mkdirs之后依旧无法创建则失败
            throw new FileNotFoundException("目标目录不可用");
        }

        // 获取目标文件
        // 获取待复制文件的文件名
        File targetFile = new File(dstDir, getItemName(srcFile));
        if (targetFile.exists()) {// 存在同名项目
            switch (operation) {
                case CONFLICT_OPERATION_TERMINATE:
                    throw new FileExistsException("指定目录下存在同名项目");

                case CONFLICT_OPERATION_APPEND_TAG:
                    targetFile = appender.getAvailableFileToken(targetFile.getAbsolutePath());// 获得一个可用的位置
                    break;

                case CONFLICT_OPERATION_COVER:
                    if (isSameItem(targetFile, srcFile)) {
                        throw new FileExistsException("无法覆盖自己");
                    }
                    break;

                case CONFLICT_OPERATION_SKIP:
                    return null;

                default:
                    throw new IllegalArgumentException("指定操作模式不合法");
            }
        }

        // 确保最终位置可用
        confirmFile(targetFile);

        // 开始复制
        IOUtil.writeData(new FileInputStream(srcFile), new FileOutputStream(targetFile));
        return targetFile;
    }

    private static File copyDir(File dstDir, File srcDir, @ConflictOperation int operation, AbsTagAppender appender) throws IOException {
        // 获取源目录
        if (!srcDir.isDirectory()) {
            // 源目录不存在或者并非目录
            throw new FileNotFoundException("源目录不可用");
        }

        // 获取源目录下的子文件和子目录，如果在目标创建之后再获取的话会造成无限递归
        File[] fileList = srcDir.listFiles();

        // 获取目标目录
        dstDir.mkdirs();
        if (!dstDir.isDirectory()) {
            // 即便在调用了mkdirs之后目标目录依然不存在
            throw new FileNotFoundException("目标目录不可用");
        }

        // 检查目录层次合法性
        if (isSameItem(srcDir, dstDir) || isItemInDir(srcDir, dstDir)) {
            throw new FileNotFoundException("不允许将目录复制到自身目录及其下级目录");
        }

        File targetDir = new File(dstDir, getItemName(srcDir));
        if (targetDir.exists()) {// 目标目录下存在同名项目
            switch (operation) {
                case CONFLICT_OPERATION_TERMINATE:
                    throw new FileExistsException("指定目录下存在同名项目");

                case CONFLICT_OPERATION_APPEND_TAG:
                    targetDir = appender.getAvailableDirToken(targetDir.getAbsolutePath());// 获得一个可用的位置
                    break;

                case CONFLICT_OPERATION_COVER:// 覆盖操作
                    // 这里不做处理
                    break;

                case CONFLICT_OPERATION_SKIP:
                    return null;

                default:
                    throw new IllegalArgumentException("指定操作模式不合法");
            }
        }

        //确保目录
        confirmDir(targetDir);

        // 开始复制所有项目
        if (fileList != null && fileList.length != 0) {
            copyItems(targetDir, fileList, operation, appender);
        }
        return targetDir;
    }

    /**
     * 复制项目。
     *
     * @param srcItem
     * @param operation
     * @param appender
     * @return
     * @throws FileNotFoundException
     * @throws FileExistsException
     * @throws IOException
     */
    public static File copyItem(File dstDir, File srcItem, @ConflictOperation int operation, AbsTagAppender appender) throws IOException {
        if (srcItem.isFile()) { // 源文件
            return copyFile(dstDir, srcItem, operation, appender);
        } else if (srcItem.isDirectory()) {
            return copyDir(dstDir, srcItem, operation, appender);
        } else {
            throw new FileNotFoundException("源项目不可用");
        }
    }

    /**
     * 批量复制文件。复制过程中某个项目可能会出现异常，导致整个操作终止。
     * 具体实现请参阅{@link #copyItem(File, File, int, AbsTagAppender)}
     *
     * @param items
     * @param operation
     * @param appender
     * @return
     * @throws FileNotFoundException
     * @throws FileExistsException
     * @throws IOException
     */
    public static File[] copyItems(File dstDir, File[] items, @ConflictOperation int operation, AbsTagAppender appender) throws IOException {
        File[] fileArr = new File[items.length];
        for (int i = 0, len = items.length; i < len; i++) {
            fileArr[i] = copyItem(dstDir, items[i], operation, appender);
        }
        return fileArr;
    }

    /**
     * 文件另存为。
     *
     * @param srcFile
     * @param dstFile
     * @param replaceIfExist
     * @return
     * @throws FileNotFoundException
     * @throws FileExistsException
     * @throws IOException
     */
    public static File saveFileAs(File srcFile, File dstFile, boolean replaceIfExist) throws IOException {
        // 获取源文件
        if (!srcFile.isFile()) {
            // 源文件不存在，或者是目录
            throw new FileNotFoundException("源文件不可用");
        }

        // 检查目标位置合法性
        if (isSameItem(srcFile, dstFile)) {
            throw new FileExistsException("源文件和目标文件相同");
        } else if ((dstFile.exists() && !replaceIfExist)) {//冲突且不允许替换
            throw new FileExistsException("目标目录下存在同名文件");
        }

        // 创建目标
        confirmFile(dstFile);

        // 开始复制
        IOUtil.writeData(new FileInputStream(srcFile), new FileOutputStream(dstFile));
        return dstFile;
    }

	/* 移动项目 */

    /**
     * 移动项目。
     * 有多重情况可能造成移动失败，比如说源项目的位置和目标目录的位置所处的文件系统不一致，
     * 比如从硬盘移动东西到U盘，可能导致移动项目失败。
     * 更多情况请参阅{@link File#renameTo(File)}。
     * 不允许将目标目录移动到自己之下，否则将抛出异常。
     *
     * @param srcItem
     * @param operation 冲突时操作。
     *                  为了避免各种意外的情况，当出现同名的情况的时候使用{@link #CONFLICT_OPERATION_COVER}操作将会抛出异常。
     * @param appender
     * @return
     * @throws FileNotFoundException
     * @throws FileExistsException
     * @throws IOException
     */
    public static File moveItem(File dstDir, File srcItem, @ConflictOperation int operation, AbsTagAppender appender) throws IOException {
        // 获取源文件
        if (!srcItem.exists()) {
            // 源文件不存在
            throw new FileNotFoundException("源项目不可用");
        }

        // 创建目标目录
        dstDir.mkdirs();
        if (!dstDir.isDirectory()) {
            // 创建之后目标目录依旧是不可用的
            throw new FileNotFoundException("目标目录不可用");
        }

        // 检查非法情况
        if (srcItem.isDirectory() && (isSameItem(srcItem, dstDir) || isItemInDir(srcItem, dstDir))) {
            throw new FileNotFoundException("无法将目录移动到自己及次级目录之下");
        }

        File targetFile = new File(dstDir, getItemName(srcItem));
        if (isSameItem(targetFile, srcItem)) {
            throw new FileExistsException("目标位置和源项目冲突，无法移动");
        }

        if (targetFile.exists()) {// 路径下存在同名文件，重命名
            switch (operation) {
                case CONFLICT_OPERATION_TERMINATE:
                    throw new FileExistsException("指定目录下存在同名项目");

                case CONFLICT_OPERATION_APPEND_TAG:
                    if (srcItem.isFile()) {
                        targetFile = appender.getAvailableFileToken(targetFile.getAbsolutePath());
                    } else {
                        targetFile = appender.getAvailableDirToken(targetFile.getAbsolutePath());
                    }
                    break;

                case CONFLICT_OPERATION_COVER:
                    throw new FileExistsException("同名文件存在时剪切项目不允许使用覆盖操作");

                case CONFLICT_OPERATION_SKIP:
                    return null;

                default:
                    throw new IllegalArgumentException("指定操作模式不合法");
            }
        }

        // 判断是否成功剪切
        if (srcItem.renameTo(targetFile)) {
            return targetFile;
        } else {
            throw new IOException("可能由于文件系统的差异或者权限不足导致移动失败");
        }
    }

    /**
     * 批量移动文件，具体实现请参阅{@link #moveItem(File, File, int, AbsTagAppender)}。
     * 移动过程某个文件可能会出现异常导致整个操作终止。
     *
     * @param srcItems
     * @param operation
     * @param appender
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws FileExistsException
     */
    public static File[] moveItems(File dstDir, File[] srcItems, @ConflictOperation int operation, AbsTagAppender appender) throws IOException {
        File[] fileArr = new File[srcItems.length];
        for (int i = 0, len = srcItems.length; i < len; i++) {
            fileArr[i] = moveItem(dstDir, srcItems[i], CONFLICT_OPERATION_APPEND_TAG, appender);
        }
        return fileArr;
    }

	/* 移动项目Ex，但移动操作失败的时候使用复制后删除的方法来实现移动 */

    /**
     * 先尝试使用{@link #moveItem(File, File, int, AbsTagAppender)}
     * 方法进行移动操作，当操作抛出{@link IOException#}时说明{@link File#renameTo(File)}方法执行不成。
     * 此时换用复制成功后删除的方式移动文件。
     * 如果使用{@link #moveItem(File, File, int, AbsTagAppender)}时抛出了{@link FileNotFoundException#}或者{@link FileExistsException#}
     * 异常的话，说明参数或者状态不合法，将直接终止操作。
     * 该方法涉及IO操作，可能没有使用{@link #moveItem(File, File, int, AbsTagAppender)}
     * 来得快，但是不受文件系统的限制。
     *
     * @param dstDir
     * @param srcItem
     * @param operation
     * @param appender
     * @return
     * @throws FileExistsException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static File moveItemEx(File dstDir, File srcItem, @ConflictOperation int operation, AbsTagAppender appender) throws IOException {
        try {
            // 使用普通的移动方法
            return moveItem(dstDir, srcItem, operation, appender);
        } catch (FileNotFoundException | FileExistsException e) {// 抛出这些异常说明参数有问题
            // 重抛出无法处理的异常，这里不需要打印栈信息
            throw e;
        } catch (IOException e) {
            // LogUtil.e(e);这里不需要打印栈信息
            // 当抛出IO异常的时候可以知道是剪切操作因为rename操作的原因失败
            // 此时使用复制成功后删除的方法操作
            File result = copyItem(dstDir, srcItem, operation, appender);
            deleteItem(srcItem);
            return result;
        }
    }

    /**
     * 使用{@link #moveItemEx(File, File, int, AbsTagAppender)}方法批量移动文件。
     * <br>
     * 复制过程中某个项目可能会出现异常，导致整个操作终止。
     *
     * @param dstDir   目标目录路径
     * @param srcItems 源项目路径
     * @param operate
     * @param appender
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static File[] moveItemsEx(File dstDir, File[] srcItems, @ConflictOperation int operate, AbsTagAppender appender) throws IOException {
        File[] fileArr = new File[srcItems.length];
        for (int i = 0, len = srcItems.length; i < len; i++) {
            fileArr[i] = moveItemEx(dstDir, srcItems[i], operate, appender);
        }
        return fileArr;
    }

	/* 删除项目 */

    /**
     * 删除项目。
     * 在删除整个目录时，若有一个子项目删除失败，则会返回false。
     *
     * @param item
     * @return
     */
    private static boolean deleteItem(File item) {
        boolean flag = true;
        if (item.isDirectory()) {// 是目录则处理子文件
            File[] files = item.listFiles();
            if (files != null) {
                for (File child : files) { // 删除该目录下的项目
                    if (!deleteItem(child)) { // 只要有一个失败就立刻不再继续
                        flag = false;
                        break;
                    }
                }
            }
        }
        flag &= item.delete();
        return flag;
    }

    /**
     * 批量删除文件。
     *
     * @param items
     * @return
     */
    public static boolean[] deleteItems(File... items) {
        boolean[] resultArr = new boolean[items.length];
        for (int i = 0, len = items.length; i < len; i++) {
            resultArr[i] = deleteItem(items[i]);
        }
        return resultArr;
    }
}