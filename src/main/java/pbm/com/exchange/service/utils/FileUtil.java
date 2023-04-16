package pbm.com.exchange.service.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import pbm.com.exchange.service.dto.FileDTO;

public class FileUtil {

    final int OPTIMAL_SIZE = 900;

    final List<String> IMAGE_LIST = Arrays.asList("jpg", "jpeg", "png", "bmp");

    private static FileUtil instance = null;

    private final Logger log = LoggerFactory.getLogger(FileUtil.class);

    private String uploadFolder;

    public String getUploadFolder() {
        return uploadFolder;
    }

    public void setUploadFolder(String uploadFolder) {
        this.uploadFolder = uploadFolder;
    }

    public static FileUtil getInstance() {
        if (instance == null) {
            instance = new FileUtil();
        }
        return instance;
    }

    public void storeFile(FileDTO fileDTO, MultipartFile file) {
        Path path = Paths.get(fileDTO.getRelativePath() + File.separator + fileDTO.getFileName());

        if (fileDTO != null) {
            try (InputStream input = file.getInputStream(); OutputStream output = new FileOutputStream(path.toFile())) {
                String ext = getExtension(fileDTO.getFileName());
                if (IMAGE_LIST.stream().anyMatch(item -> item.equalsIgnoreCase(ext))) {
                    BufferedImage newImage = compressImage(input);
                    ImageIO.write(newImage, ext, output);
                } else {
                    log.info("save file to : " + path.toString());
                    if (!Files.exists(path.getParent())) {
                        // Create directory if it doesn't exist
                        Files.createDirectories(path.getParent());
                    }
                    doCopy(input, output);
                }
            } catch (IOException e) {
                if (log.isDebugEnabled()) {
                    e.printStackTrace();
                }
                log.error(e.getMessage());
            } catch (Throwable e) {
                if (log.isDebugEnabled()) {
                    e.printStackTrace();
                }
                log.error(e.getMessage());
            }
        }
    }

    private String getExtension(String filename) {
        return Optional.ofNullable(filename).filter(f -> f.contains(".")).map(f -> f.substring(filename.lastIndexOf(".") + 1)).orElse("");
    }

    private BufferedImage compressImage(InputStream file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        return scaleImage(image, OPTIMAL_SIZE);
    }

    private BufferedImage scaleImage(BufferedImage bufferedImage, int size) {
        double boundSize = size;
        int origWidth = bufferedImage.getWidth();
        int origHeight = bufferedImage.getHeight();
        double scale;
        if (origHeight > origWidth) scale = boundSize / origHeight; else scale = boundSize / origWidth;
        //* Don't scale up small images.
        if (scale > 1.0) return (bufferedImage);
        int scaledWidth = (int) (scale * origWidth);
        int scaledHeight = (int) (scale * origHeight);
        Image scaledImage = bufferedImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaledBI.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();
        return (scaledBI);
    }

    private void doCopy(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[64];
        int numBytes;
        while ((numBytes = is.read(bytes)) != -1) {
            os.write(bytes, 0, numBytes);
        }
        os.flush();
        os.close();
        is.close();
    }

    public FileDTO createFile(MultipartFile file) {
        String folderPath = FileUtil.getInstance().getUploadFolder();
        String fileName = getRandomFileName() + "." + getExtension(file.getOriginalFilename());
        FileDTO fileDTO = new FileDTO();
        fileDTO.setFileName(fileName);
        fileDTO.setFileOnServer(folderPath + File.separator + fileName);
        fileDTO.setRelativePath(folderPath);
        return fileDTO;
    }

    public String getRandomFileName() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
