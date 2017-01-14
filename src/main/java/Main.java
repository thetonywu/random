import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.*;

/**
 * Created by tony on 1/13/17.
 * Generate a RSA pair using random numbers from Random.org
 */
public class Main {

    static final int IMAGE_SIZE = 8; // reduced image size because I keep surpassing the api limit...
    static final String OUTPUT_IMG_NAME = "savedRandomImage.bmp";

    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");

        RandomOrg randomOrg = new RandomOrg();

        keyGen.initialize(1024, randomOrg);

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();

        BASE64Encoder b64 = new BASE64Encoder();

        System.out.println("Private Key:");
        System.out.println(b64.encode(priv.getEncoded()));
        System.out.println("");

        System.out.println("Public Key:");
        System.out.println(b64.encode(pub.getEncoded()));

        BufferedImage img = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);

        for (int i =0; i<IMAGE_SIZE; i++) {
            for (int j=0; j<IMAGE_SIZE; j++) {
                int rgb = new Color(randomOrg.nextRGB(), randomOrg.nextRGB(), randomOrg.nextRGB()).getRGB();
                img.setRGB(i,j,rgb);
            }
        }

        File outputfile = new File(OUTPUT_IMG_NAME);
        ImageIO.write(img, "bmp", outputfile);

    }
}
