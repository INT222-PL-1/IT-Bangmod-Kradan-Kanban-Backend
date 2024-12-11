package sit.int221.itbkkbackend.utils;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

@Slf4j
public class VideoThumbnailGenerator {
    public static void createVideoThumbnail(String videoPath, String thumbnailPath) throws Exception {
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoPath);
        frameGrabber.start();

        // Seek to 1-second mark (or other desired timestamp)
        frameGrabber.setFrameNumber(1);

        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage frame = converter.convert(frameGrabber.grabImage());

        if (frame != null) {

            int originalWidth = frame.getWidth();
            int originalHeight = frame.getHeight();

            double widthRatio = (double) 100 / originalWidth;
            double heightRatio = (double) 100 / originalHeight;
            double scaleFactor = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (originalWidth * scaleFactor);
            int newHeight = (int) (originalHeight * scaleFactor);

            BufferedImage resizedFrame = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedFrame.createGraphics();
            g.drawImage(frame, 0, 0, newWidth, newHeight, null);
            g.dispose();

            ImageIO.write(resizedFrame, "jpg", new File(thumbnailPath));

        } else {
            log.error("Failed creating thumbnail for video: {}", videoPath);
        }

        converter.close();

        frameGrabber.stop();
        frameGrabber.close();
    }
}
