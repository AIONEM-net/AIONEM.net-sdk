package aionem.net.sdk.web.dao;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.beans.DaoRes;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.beans.Resource;
import aionem.net.sdk.web.config.ConfEnv;
import aionem.net.sdk.web.utils.UtilsDrive;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;


@Log4j2
public class DriveManager {

    private static DriveManager driveManager;
    public static DriveManager getInstance() {
        if(driveManager == null) {
            driveManager = new DriveManager();
        }
        return driveManager;
    }

    public DriveManager() {

    }

    public DaoRes uploadFile(final Part filePart, String uploadName) {

        final DaoRes resUpload = new DaoRes();

        try {

            if(filePart != null) {

                final InputStream fileStream = filePart.getInputStream();
                final String fileName = UtilsText.notEmpty(filePart.getName(), filePart.getName());
                uploadName = UtilsText.notEmpty(uploadName, fileName);

                return uploadFile(fileStream, uploadName, fileName);

            }else {
                resUpload.setError("Invalid file");
            }

        }catch(final Exception e) {
            resUpload.setException(e);
        }

        return resUpload;
    }

    public DaoRes uploadFile(final File file, final String uploadName) {

        final DaoRes resUpload = new DaoRes();

        try {

            if(file.exists() && file.isFile()) {
                return uploadFile(new FileInputStream(file), uploadName, file.getName());
            }else {
                resUpload.setError("Invalid file");
            }

        }catch(final Exception e) {
            resUpload.setException(e);
        }

        return resUpload;
    }

    public DaoRes uploadFile(final InputStream fileStream, String uploadName, final String fileName) {

        final DaoRes resUpload = new DaoRes();

        try {

            if(fileStream != null) {

                String fileExtension = UtilsDrive.getFileExtension(uploadName);

                if(UtilsText.isEmpty(fileExtension)) {
                    fileExtension = UtilsDrive.getFileExtension(fileName);
                    uploadName = uploadName + (!uploadName.endsWith(".") ? "." : "") + fileExtension;
                }

                final String fileFolder = UtilsDrive.getFileFolder(fileExtension);
                final String filePath = UtilsResource.path(fileFolder, uploadName);
                final String fileUrl = ConfEnv.getInstance().getContextPath(ResourceResolver.DRIVE_PATH_UPLOADS) +"/"+ filePath;

                final Resource fileDirectory = new Resource(UtilsResource.getRealPathRoot(ResourceResolver.DRIVE_PATH_UPLOADS +"/"+ fileFolder));

                final boolean isDirectory;
                if(!fileDirectory.exists()) {
                    isDirectory = fileDirectory.getFile().mkdirs();
                }else {
                    isDirectory = true;
                }

                if(isDirectory) {

                    final Resource file = new Resource(fileDirectory.getRealPath() + "/" + uploadName);
                    final FileOutputStream outputStream = file.getFileOutputStream();

                    final byte[] buffer = new byte[4096];
                    int readSize;
                    while((readSize = fileStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, readSize);
                    }
                    fileStream.close();

                    final long fileSize = file.getSize();

                    resUpload.setSuccess(true);

                    resUpload.put(UtilsDrive.DRIVE_FILE, fileName);
                    resUpload.put(UtilsDrive.DRIVE_FILE_NAME, uploadName);
                    resUpload.put(UtilsDrive.DRIVE_FILE_PATH, filePath);
                    resUpload.put(UtilsDrive.DRIVE_FILE_URL, fileUrl);
                    resUpload.put(UtilsDrive.DRIVE_FILE_SIZE, fileSize);
                    resUpload.put(UtilsDrive.DRIVE_FILE_EXTENSION, fileExtension);

                }else {
                    resUpload.setError("Directory doesn't exist");
                }

            }else {
                resUpload.setError("Invalid file");
            }

        }catch(final Exception e) {
            resUpload.setException(e);
        }

        return resUpload;
    }

    public Resource getFolder() {
        return getFile("");
    }

    public Resource getFile(final String path) {
        return ResourceResolver.getResourceDrive(path);
    }

    public ArrayList<Resource> getFiles() {
        return getFiles("");
    }

    public ArrayList<Resource> getFiles(final String path) {
        final ArrayList<Resource> listFiles = new ArrayList<>();
        for(final Resource file : getFile(path).children()) {
            if(file.exists()) {
                listFiles.add(file);
            }
        }
        return listFiles;
    }

    public DaoRes move(final Resource drive, final String pathNew) {
        return move(drive, pathNew, drive.getName());
    }

    public DaoRes move(final Resource drive, final String pathNew, final String nameNew) {
        
        final DaoRes resMove = new DaoRes();
        
        try {
            
            if(!drive.isDrive()) {
                resMove.setError("Not a drive");
            }else if(!drive.exists()) {
                resMove.setError("Drive doesn't exist");
            }else {

                final String pathNameNew = UtilsResource.path(pathNew, nameNew);

                final Path pathSource = Paths.get(drive.getPathReal());
                final Path pathDestination = Paths.get(ResourceResolver.getRealPathPage(pathNameNew));

                Files.walkFileTree(pathSource, new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                        if (!dir.equals(pathSource)) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        final Path targetDir = pathDestination.resolve(pathSource.relativize(dir));
                        Files.createDirectories(targetDir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                        final Path targetFile = pathDestination.resolve(pathSource.relativize(file));
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        file.toFile().delete();
                        return FileVisitResult.CONTINUE;
                    }

                });

                final int references = references(drive, ResourceResolver.getResourceDrive(pathNameNew), true);

                resMove.setSuccess(true);
                resMove.put("references", references);
            }
        }catch(final Exception e) {
            resMove.setException(e);
        }
        
        return resMove;
    }

    public DaoRes copy(final Resource drive, final String pathNew) {
        return copy(drive, pathNew, drive.getName(), false);
    }

    public DaoRes copy(final Resource drive, final String pathNew, final boolean excludeChildren) {
        return copy(drive, pathNew, drive.getName(), excludeChildren);
    }

    public DaoRes copy(final Resource drive, final String pathNew, final String nameNew, final boolean excludeChildren) {

        final DaoRes resCopy = new DaoRes();

        try {

            if(!drive.isDrive()) {
                resCopy.setError("Not a drive");
            }else if(!drive.exists()) {
                resCopy.setError("Drive doesn't exist");
            }else {

                final String pathNameNew = UtilsResource.path(pathNew, nameNew);

                final Path pathSource = Paths.get(drive.getRealPath());
                final Path pathDestination = Paths.get(ResourceResolver.getRealPathDrive(pathNameNew));

                Files.walkFileTree(pathSource, new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                        if (excludeChildren && !dir.equals(pathSource)) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        final Path targetDir = pathDestination.resolve(pathSource.relativize(dir));
                        Files.createDirectories(targetDir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                        final Path targetFile = pathDestination.resolve(pathSource.relativize(file));
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }

                });

                final int references = references(drive, ResourceResolver.getResourceDrive(pathNameNew), true);

                resCopy.setSuccess(true);
                resCopy.put("references", references);
            }

        }catch(final Exception e) {
            resCopy.setException(e);
        }
        
        return resCopy;
    }

    public int references(final Resource resource, final Resource resourceNew, final boolean update) {
        final int[] totalReferences = {0};

        final Path pathSection = Paths.get(ResourceResolver.getRealPathPage());

        try {
            Files.walkFileTree(pathSection, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
                    if(Files.isRegularFile(file)) {
                        final int references = ResourceResolver.referenceDrives(new Resource(file), "ui.drive"+ resource.getRelativePath(), "ui.drive"+ resourceNew.getRelativePath(), update);
                        totalReferences[0] += references;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("Error updating references: {}", e.toString());
        }

        return totalReferences[0];
    }

}
