package stegano.dct.net.f5;

import stegano.dct.algo.JpegEncoder;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import stegano.dct.net.f5.image.Bmp;

public class Embed {
    public static void main(final String args[]) {
        Image image = null;
        FileOutputStream dataOut = null;
        File file, outFile;
        JpegEncoder jpg;
        int i, Quality = 80;
        boolean haveInputImage = false;
        String embFileName = null;
        String comment = "JPEG Encoder Copyright 1998, James R. Weeks and BioElectroMech.  ";
        String password = "abc123";
        String inFileName = null;
        String outFileName = null;
        if (args.length < 1) {
            StandardUsage();
            return;
        }
        for (i = 0; i < args.length; i++) {
            if (!args[i].startsWith("-")) {
                if (!haveInputImage) {
                    if (!args[i].endsWith(".jpg") && !args[i].endsWith(".tif") && !args[i].endsWith(".gif")
                            && !args[i].endsWith(".bmp")) {
                        StandardUsage();
                        return;
                    }
                    inFileName = args[i];
                    outFileName = args[i].substring(0, args[i].lastIndexOf(".")) + ".jpg";
                    haveInputImage = true;
                } else {
                    outFileName = args[i];
                    if (outFileName.endsWith(".tif") || outFileName.endsWith(".gif") || outFileName.endsWith(".bmp")) {
                        outFileName = outFileName.substring(0, outFileName.lastIndexOf("."));
                    }
                    if (!outFileName.endsWith(".jpg")) {
                        outFileName = outFileName.concat(".jpg");
                    }
                }
                continue;
            }
            if (args.length < i + 1) {
                System.out.println("Missing parameter for switch " + args[i]);
                StandardUsage();
                return;
            }
            if (args[i].equals("-e")) {
                embFileName = args[i + 1];
            } else if (args[i].equals("-p")) {
                password = args[i + 1];
            } else if (args[i].equals("-q")) {
                try {
                    Quality = Integer.parseInt(args[i + 1]);
                } catch (final NumberFormatException e) {
                    StandardUsage();
                    return;
                }
            } else if (args[i].equals("-c")) {
                comment = args[i + 1];
            } else {
                System.out.println("Unknown switch " + args[i] + " ignored.");
            }
            i++;
        }
        outFile = new File(outFileName);
        i = 1;
        while (outFile.exists()) {
            outFile = new File(outFileName.substring(0, outFileName.lastIndexOf(".")) + i++ + ".jpg");
            if (i > 100) {
                System.exit(0);
            }
        }
        file = new File(inFileName);
        if (file.exists()) {
            try {
                dataOut = new FileOutputStream(outFile);
            } catch (final IOException e) {
            }
            if (inFileName.endsWith(".bmp")) {
                final Bmp bmp = new Bmp(inFileName);
                image = bmp.getImage();
            } else {
                image = Toolkit.getDefaultToolkit().getImage(inFileName);
            }
            jpg = new JpegEncoder(image, Quality, dataOut, comment);
            try {
                if (embFileName == null) {
                    jpg.Compress();
                } else {
                    jpg.Compress(new FileInputStream(embFileName), password);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
            try {
                dataOut.close();
            } catch (final IOException e) {
            }
        } else {
            System.out.println("I couldn't find " + inFileName + ". Is it in another directory?");
        }
    }

    public static void StandardUsage() {
        System.out.println("F5/JpegEncoder for Java(tm)");
        System.out.println("");
        System.out.println("Program usage: java Embed [Options] \"InputImage\".\"ext\" [\"OutputFile\"[.jpg]]");
        System.out.println("");
        System.out.println("You have the following options:");
        System.out.println("-e <file to embed>\tdefault: embed nothing");
        System.out.println("-p <password>\t\tdefault: \"abc123\", only used when -e is specified");
        System.out.println("-q <quality 0 ... 100>\tdefault: 80");
        System.out.println(
                "-c <comment>\t\tdefault: \"JPEG Encoder Copyright 1998, James R. Weeks and BioElectroMech.  \"");
        System.out.println("");
        System.out.println("\"InputImage\" is the name of an existing image in the current directory.");
        System.out.println("  (\"InputImage may specify a directory, too.) \"ext\" must be .tif, .gif,");
        System.out.println("  or .jpg.");
        System.out.println("Quality is an integer (0 to 100) that specifies how similar the compressed");
        System.out.println("  image is to \"InputImage.\"  100 is almost exactly like \"InputImage\" and 0 is");
        System.out.println("  most dissimilar.  In most cases, 70 - 80 gives very good results.");
        System.out.println("\"OutputFile\" is an optional argument.  If \"OutputFile\" isn't specified, then");
        System.out.println("  the input file name is adopted.  This program will NOT write over an existing");
        System.out.println("  file.  If a directory is specified for the input image, then \"OutputFile\"");
        System.out.println("  will be written in that directory.  The extension \".jpg\" may automatically be");
        System.out.println("  added.");
        System.out.println("");
        System.out.println("Copyright 1998 BioElectroMech and James R. Weeks.  Portions copyright IJG and");
        System.out.println("  Florian Raemy, LCAV.  See license.txt for details.");
        System.out.println("Visit BioElectroMech at www.obrador.com.  Email James@obrador.com.");
        System.out.println("Steganography added by Andreas Westfeld, westfeld@inf.tu-dresden.de");
    }
}
