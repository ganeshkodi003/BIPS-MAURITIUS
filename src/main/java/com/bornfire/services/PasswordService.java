package com.bornfire.services;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
//import com.bornfire.entity.BIPS_MerUserManagement_Repo;
//import com.bornfire.entity.BIPS_Mer_User_Management_Entity;
//import com.bornfire.entity.BIPS_PasswordManagement_Repo;
//import com.bornfire.entity.BIPS_Password_Management_Entity;

@Service
public class PasswordService {
	
	 private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		/*
		 * @Autowired private BIPS_PasswordManagement_Repo bIPS_PasswordManagement_Repo;
		 * 
		 * @Autowired private BIPS_MerUserManagement_Repo bIPS_MerUserManagement_Repo;
		 */
	    
	  

	    public String  registerUser(String password) {
	        // Encrypt the password using BCrypt
	     String  encryptedPassword = passwordEncoder.encode(password);
	     return encryptedPassword;
	         
	  
	    }
	    
	    public String userRegister(String password) {
	        // Encrypt the password using BCrypt
	    	String  encryptedPassword = passwordEncoder.encode(password);
		     return encryptedPassword;
	    }
	    
	    public String generateQRImage(String text) {
	        // Encrypt the password using BCrypt
	    	try {
				// Create a QR code writer
				QRCodeWriter qrCodeWriter = new QRCodeWriter();

				// Encode the text into a QR code with a larger size (e.g., 500x500 pixels) and
				// error correction
				BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500);

				// Convert the BitMatrix to a BufferedImage
				BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

				// Load the logo image from the resources directory
				ClassLoader classLoader = getClass().getClassLoader();
				InputStream logoInputStream = classLoader.getResourceAsStream("static/images/bornfireplain.png");
				BufferedImage logo = ImageIO.read(logoInputStream);

				// Calculate the size for the logo (e.g., 12% of the QR code size)
				int logoSize = (int) (qrCodeImage.getWidth() * 0.12);

				// Create a new BufferedImage to hold the composite image
				BufferedImage combined = new BufferedImage(qrCodeImage.getWidth(), qrCodeImage.getHeight(),
						BufferedImage.TYPE_INT_ARGB);

				// Draw the QR code onto the combined image
				Graphics2D g = combined.createGraphics();
				g.drawImage(qrCodeImage, 0, 0, null);

				// Calculate the position to place the logo in the center of the QR code
				int logoX = (combined.getWidth() - logoSize) / 2;
				int logoY = (combined.getHeight() - logoSize) / 2;

				// Draw a blank space for the logo in the center
				g.setColor(Color.WHITE);
				g.fillRect(logoX, logoY, logoSize, logoSize);

				// Draw the logo onto the blank space
				g.drawImage(logo, logoX, logoY, logoSize, logoSize, null);

				// Dispose of the graphics object
				g.dispose();

				// Convert the combined image to a byte array
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ImageIO.write(combined, "PNG", outputStream);
				byte[] qrCodeBytes = outputStream.toByteArray();

				// Encode the byte array as a Base64 string with a PNG data URI prefix
				return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCodeBytes);
			} catch (WriterException | IOException e) {
				// Handle exceptions
				e.printStackTrace();
				return null;
			}
	    }
	    
	    public String generateQRCode(String content) {
	    	try {
				// Create a QR code writer
				QRCodeWriter qrCodeWriter = new QRCodeWriter();

				// Encode the text into a QR code with a larger size (e.g., 500x500 pixels) and
				// error correction
				BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 500, 500);

				// Convert the BitMatrix to a BufferedImage
				BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

				// Load the logo image from the resources directory
				ClassLoader classLoader = getClass().getClassLoader();
				InputStream logoInputStream = classLoader.getResourceAsStream("static/images/bornfireplain.png");
				BufferedImage logo = ImageIO.read(logoInputStream);

				// Calculate the size for the logo (e.g., 12% of the QR code size)
				int logoSize = (int) (qrCodeImage.getWidth() * 0.1);

				// Create a new BufferedImage to hold the composite image
				BufferedImage combined = new BufferedImage(qrCodeImage.getWidth(), qrCodeImage.getHeight(),
						BufferedImage.TYPE_INT_ARGB);

				// Draw the QR code onto the combined image
				Graphics2D g = combined.createGraphics();
				g.drawImage(qrCodeImage, 0, 0, null);

				// Calculate the position to place the logo in the center of the QR code
				int logoX = (combined.getWidth() - logoSize) / 2;
				int logoY = (combined.getHeight() - logoSize) / 2;

				// Draw a blank space for the logo in the center
				g.setColor(Color.WHITE);
				g.fillRect(logoX, logoY, logoSize, logoSize);

				// Draw the logo onto the blank space
				g.drawImage(logo, logoX, logoY, logoSize, logoSize, null);

				// Dispose of the graphics object
				g.dispose();

				// Convert the combined image to a byte array
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ImageIO.write(combined, "PNG", outputStream);
				byte[] qrCodeBytes = outputStream.toByteArray();

				// Encode the byte array as a Base64 string with a PNG data URI prefix
				return "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCodeBytes);
			} catch (WriterException | IOException e) {
				// Handle exceptions
				e.printStackTrace();
				return null;
			}
		}
	    

	    
	}