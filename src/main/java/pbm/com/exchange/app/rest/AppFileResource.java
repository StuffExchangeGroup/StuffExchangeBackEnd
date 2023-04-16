package pbm.com.exchange.app.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import liquibase.pro.packaged.m;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pbm.com.exchange.domain.File;
import pbm.com.exchange.repository.FileRepository;
import pbm.com.exchange.web.rest.FileResource;

@RestController
@RequestMapping("api/app")
public class AppFileResource {

    private final Logger log = LoggerFactory.getLogger(FileResource.class);

    @Autowired
    private FileRepository fileRepository;

    @GetMapping(value = "/image/download/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void getImage(@PathVariable("id") Long id, HttpServletResponse response, final HttpServletRequest request) {
        log.debug("Rest request to get image by file id {} ", id);

        Optional<File> file = fileRepository.findById(id);
        if (file.isPresent()) {
            String relativePath = file.get().getRelativePath() + java.io.File.separator + file.get().getFileName();
            java.io.File f = new java.io.File(relativePath);
            try (InputStream is = new FileInputStream(f); OutputStream outputStream = response.getOutputStream()) {
                String contentType = getFileContentType(relativePath);
                if (StringUtils.isEmpty(contentType)) {
                    contentType = "image/gif";
                }
                response.reset();
                response.setContentType(contentType);
                response.setHeader("Content-Disposition", "inline; filename=" + file.get().getFileName());
                response.addHeader("Cache-Control", "max-age=86400, public");
                response.addHeader("Pragma", "public");

                Calendar calendar = Calendar.getInstance();
                // add one day to the date/calendar
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                // now get "tomorrow"
                Date tomorrow = calendar.getTime();
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                response.addHeader("Expires", df.format(tomorrow));
                response.setStatus(HttpServletResponse.SC_OK);
                IOUtils.copy(is, outputStream);
            } catch (Exception e) {
                log.debug("File is not stored at id: {}", id);
            }
        } else {
            log.debug("File not found at id: {}", id);
        }
    }

    public static String getFileContentType(String str) {
        Path path = Paths.get(str);
        try {
            return Files.probeContentType(path);
        } catch (IOException e) {
            MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
            return mimetypesFileTypeMap.getContentType(str);
        }
    }
}
