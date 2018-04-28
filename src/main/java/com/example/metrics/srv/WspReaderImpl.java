package com.example.metrics.srv;

import com.example.metrics.entity.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class WspReaderImpl implements WspReader {

    private static final int METADATA_SIZE_IN_BYTES = 16;
    private static final int ARCHIVE_INFO_SIZE_IN_BYTES = 12;
    private static final int DATAPOINT_SIZE_IN_BYTES = 12;

    private String dataRootPath = "C:\\TEMP\\";

    @Override
    public Series getSeriesByWspFilePath(String seriesId) {
        Path path = Paths.get(dataRootPath, seriesId);
        Deque<Datapoint> resultDatapoints = new LinkedList<>();
        Series series = null;
        try (ReadableByteChannel byteChannel = Files.newByteChannel(path)) {
            Header header = getHeader(byteChannel);
            List<Archive> archives = getArchives(byteChannel, header.getArchiveInfos());
            series = new Series(seriesId, header, archives);
        } catch (IOException e) {
            System.out.println("I/O Error " + e);
        }

        return series;
    }

    private List<Archive> getArchives(ReadableByteChannel byteChannel, List<ArchiveInfo> archiveInfos) throws IOException {
        List<Archive> archives = new ArrayList<>();
        for(ArchiveInfo archiveInfo : archiveInfos){
            archives.add(getArchive(byteChannel, archiveInfo));
        }
        return archives;
    }
    private Archive getArchive(ReadableByteChannel byteChannel, ArchiveInfo archiveInfo) throws IOException {
        int archiveSize = (int) (DATAPOINT_SIZE_IN_BYTES * archiveInfo.getPoints());
        ByteBuffer buf = ByteBuffer.allocate(archiveSize);
        byteChannel.read(buf);
        buf.rewind();
        Deque<Datapoint> datapoints = new LinkedList<>();
        do {
            long timeStamp = buf.getInt();
            if (timeStamp == 0) continue;
            double value = buf.getDouble();
            if (value == 0.0) continue;
            Datapoint datapoint = new Datapoint(new Date(timeStamp * 1000), value);
            datapoints.addLast(datapoint);
        } while (buf.hasRemaining());

        return new Archive(archiveInfo, datapoints);
    }

    private Header getHeader(ReadableByteChannel byteChannel) throws IOException {
        Metadata metadata = getMetadata(byteChannel);
        List<ArchiveInfo> archiveInfos = getArchiveInfos(byteChannel, metadata.getArchiveCount());
        return new Header(metadata, archiveInfos);
    }

    private Metadata getMetadata(ReadableByteChannel byteChannel) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(METADATA_SIZE_IN_BYTES);
        byteChannel.read(buf);
        buf.rewind();
        return Metadata.Builder.newInstance()
                .aggregationType(getUnsignedLongFrom4Bytes(buf))
                .maxRetention(getUnsignedLongFrom4Bytes(buf))
                .xFilesFactor(buf.getFloat())
                .archiveCount(buf.getInt())
                .build();
    }

    private List<ArchiveInfo> getArchiveInfos(ReadableByteChannel byteChannel, int archiveCount) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(ARCHIVE_INFO_SIZE_IN_BYTES * archiveCount);
        byteChannel.read(buf);
        buf.rewind();

        List<ArchiveInfo> archiveInfos = new ArrayList<>();

        for (int i = 1; i <= archiveCount; i++) {
            ArchiveInfo archiveInfo = ArchiveInfo.Builder.newInstance()
                    .offset(getUnsignedLongFrom4Bytes(buf))
                    .secondsPerPoint(getUnsignedLongFrom4Bytes(buf))
                    .points(getUnsignedLongFrom4Bytes(buf))
                    .build();
            archiveInfos.add(archiveInfo);
        }

        return archiveInfos;
    }

    private long getUnsignedLongFrom4Bytes(ByteBuffer buf) {
        return ((long) buf.getInt()) & 0xffffffffffffffffl;
    }
}
