package aionem.net.sdk.web.dao;

import aionem.net.sdk.core.utils.UtilsText;
import aionem.net.sdk.data.DaoRes;
import aionem.net.sdk.data.utils.UtilsResource;
import aionem.net.sdk.web.modals.ConfEnv;
import aionem.net.sdk.web.modals.Resource;
import aionem.net.sdk.web.utils.UtilsDrive;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;


@Log4j2
public class DriveManager {

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

        }catch(Exception e) {
            resUpload.setException(e);
        }

        return resUpload;
    }

    public DaoRes uploadFile(final File file, String uploadName) {

        final DaoRes resUpload = new DaoRes();

        try {

            if(file.exists() && file.isFile()) {

                return uploadFile(new FileInputStream(file), uploadName, file.getName());

            }else {
                resUpload.setError("Invalid file");
            }

        }catch(Exception e) {
            resUpload.setException(e);
        }

        return resUpload;
    }

    public DaoRes uploadFile(final InputStream fileStream, String uploadName, String fileName) {

        final DaoRes resUpload = new DaoRes();

        try {

            if(fileStream != null) {

                String fileExtension = UtilsDrive.getFileExtension(uploadName);

                if(UtilsText.isEmpty(fileExtension)) {
                    fileExtension = UtilsDrive.getFileExtension(fileName);
                    uploadName = uploadName + (!uploadName.endsWith(".") ? "." : "") + fileExtension;
                }

                final String fileFolder = UtilsDrive.getFileFolder(fileExtension);
                final String filePath = fileFolder +"/" + uploadName;
                final String fileUrl = ConfEnv.getInstance().getContextPath(UtilsDrive.DRIVE_API_PATH) +"/"+ filePath;

                final File fileDirectory = new File(UtilsResource.getRealPathRoot(UtilsDrive.DRIVE_API_PATH +"/"+ fileFolder));

                final boolean isDirectory;
                if(!fileDirectory.exists()) {
                    isDirectory = fileDirectory.mkdirs();
                }else {
                    isDirectory = true;
                }

                if(isDirectory) {

                    final File file = new File(fileDirectory.getPath() + "/" + uploadName);
                    final FileOutputStream outputStream = new FileOutputStream(file);

                    final byte[] buffer = new byte[4096];
                    int readSize;
                    while((readSize = fileStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, readSize);
                    }
                    fileStream.close();

                    final long fileSize = file.length();

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

        }catch(Exception e) {
            resUpload.setException(e);
        }

        return resUpload;
    }

    public File getFolder() {
        return getFile("");
    }

    public File getFile(final String path) {
        return UtilsResource.getRealFileRoot("/ui.drive" +"/" + path);
    }

    public ArrayList<File> getFiles() {
        return getFiles("");
    }

    public ArrayList<File> getFiles(final String path) {
        final ArrayList<File> listFiles = new ArrayList<>();
        final File[] files = getFile(path).listFiles();
        if(files != null) {
            for(final File file : files) {
                if(file.exists()) {
                    listFiles.add(file);
                }
            }
        }
        return listFiles;
    }

    public int references(final Resource resource, final Resource resourceNew, final boolean update) {
        final int[] totalReferences = {0};

        final Path pathSection = Paths.get(ResourceResolver.getRealPathPage());

        try {
            Files.walkFileTree(pathSection, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (Files.isRegularFile(file)) {
                        final int references = ResourceResolver.references(file, "ui.drive"+ resource.getPath(), "ui.drive"+ resourceNew.getPath(), update);
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
