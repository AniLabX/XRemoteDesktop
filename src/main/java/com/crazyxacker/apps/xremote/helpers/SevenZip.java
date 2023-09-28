package com.crazyxacker.apps.xremote.helpers;

import com.crazyxacker.apps.xremote.utils.Utils;
import net.greypanther.natsort.CaseInsensitiveSimpleNaturalComparator;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SevenZip implements Closeable {
    private RandomAccessFile randomAccessFile;
    private IInArchive inArchive;

    private ListIterator<ISimpleInArchiveItem> iterator;
    private ISimpleInArchiveItem entry;

    public static void unpack(File inputFile, String outputDir) {
        unpack(inputFile.getAbsolutePath(), outputDir);
    }

    public static void unpack(String inputFilePath, String outputDir) {
        SevenZip iterator = new SevenZip();
        try {
            iterator.open(inputFilePath);
            while (iterator.next()) {
                String entryPath = iterator.getEntryPath();
                InputStream in = iterator.getEntryInputStream();

                String entryPathWithoutFile = Utils.getPath(entryPath);
                File outFile = new File(outputDir + entryPathWithoutFile);
                outFile.mkdirs();

                OutputStream out = new FileOutputStream(new File(outFile, Utils.getFileNameWithExt(entryPath)));
                IOUtils.copy(in, out);

                Utils.closeQuietly(in);
                Utils.closeQuietly(out);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Unable to create temp dir");
        } catch (SevenZipException e) {
            e.printStackTrace();
            System.err.println("Unable to read archive stream");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to open archive or unpack file");
        } finally {
            Utils.closeQuietly(iterator);
        }
    }

    public void open(String archivePath) throws IOException {
        System.out.println("[INFO] [SevenZipIterator] Reading archive: " + archivePath);
        // Открытие архива для чтения

        randomAccessFile = new RandomAccessFile(archivePath, "r");
        inArchive = net.sf.sevenzipjbinding.SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));

        reset();
    }

    public void reset() throws IOException {
        iterator = Arrays.stream(inArchive.getSimpleInterface().getArchiveItems())
                .filter(it -> {
                    try {
                        return !it.isFolder();
                    } catch (SevenZipException e) {
                        return false;
                    }
                })
                .sorted((entry1, entry2) -> {
                    try {
                        return CaseInsensitiveSimpleNaturalComparator.getInstance().compare(entry1.getPath().toLowerCase(), entry2.getPath().toLowerCase());
                    } catch (SevenZipException e) {
                        return 0;
                    }
                })
                .collect(Collectors.toList())
                .listIterator();
    }

    public boolean next() {
        if (iterator.hasNext()) {
            entry = iterator.next();
            return true;
        }
        return false;
    }

    public String getEntryPath() {
        try {
            return entry.getPath();
        } catch (SevenZipException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
        iterator = null;
        entry = null;
        Utils.closeQuietly(randomAccessFile);
        Utils.closeQuietly(inArchive);
    }

    public InputStream getEntryInputStream() throws SevenZipException {
        List<ByteArrayInputStream> arrayInputStreams = new ArrayList<>();
        entry.extractSlow(new SevenZipOutputStream(arrayInputStreams));
        return new SequenceInputStream(Collections.enumeration(arrayInputStreams));
    }

    private record SevenZipOutputStream(List<ByteArrayInputStream> arrayInputStreams) implements ISequentialOutStream {
        @Override
        public int write(byte[] data) {
            arrayInputStreams.add(new ByteArrayInputStream(data));
            return data.length;
        }
    }
}
