package file;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

public class Thumbnail {
	public String thumbnailMake(String path, String fileName) {
		String newFileName = "";
		
		System.out.println("thumbnailMake : " + path + " , " + fileName);

		try {
			int maxSize = 500;
			int thumbnail_width = maxSize;
			int thumbnail_height = maxSize;
			File origin_file_name = new File(fileName);

			String ext = getFileExt(fileName);
			newFileName = fileName.replace("." + ext, ".thumbnail." + ext);
			
			System.out.println("originalFile : " + fileName + "\n newFileName : " + newFileName );

			BufferedImage buffer_original_image = ImageIO.read(origin_file_name);

			double imgWidth = buffer_original_image.getWidth();
			double imgHeight = buffer_original_image.getHeight();

			if (imgWidth < imgHeight) {
				thumbnail_width = (int) ((imgWidth / imgHeight) * maxSize);
			} else {
				thumbnail_height = (int) ((imgHeight / imgWidth) * maxSize);
			}

			int imgType = (buffer_original_image.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
					: BufferedImage.TYPE_INT_ARGB;
			BufferedImage buffer_thumbnail_image = new BufferedImage(thumbnail_width, thumbnail_height, imgType);
			Graphics2D graphic = buffer_thumbnail_image.createGraphics();

			graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphic.drawImage(buffer_original_image, 0, 0, thumbnail_width, thumbnail_height, null);

			if (ext.equalsIgnoreCase("jpg")) {
				writeJpeg(buffer_thumbnail_image, newFileName, 1.0f);
			} else {
				File thumb_file_name = new File(newFileName);
				ImageIO.write(buffer_thumbnail_image, ext.toLowerCase(), thumb_file_name);
			}
			System.out.println("변환완료 -> newFileName : " + newFileName);

			graphic.dispose();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		File f = new File(newFileName);
		String fName = f.getName();
		
		return fName;
	}

	private static String getFileExt(String fileName) { // "abc.txt" -> "txt", not ".txt"
		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
		if (i > p) {
			return fileName.substring(i + 1);
		}
		return null;
	}

	private static void writeJpeg(BufferedImage image, String destFile, float quality) throws IOException {
		ImageWriter writer = null;
		FileImageOutputStream output = null;

		try {
			writer = ImageIO.getImageWritersByFormatName("jpeg").next();

			ImageWriteParam param = writer.getDefaultWriteParam();

			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(quality);

			output = new FileImageOutputStream(new File(destFile));
			writer.setOutput(output);

			IIOImage iioImage = new IIOImage(image, null, null);
			writer.write(null, iioImage, param);
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (writer != null) {
				writer.dispose();
			}

			if (output != null) {
				output.close();
			}
		}
	}
}