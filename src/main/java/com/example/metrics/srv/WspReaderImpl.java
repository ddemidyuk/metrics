package com.example.metrics.srv;

import com.example.metrics.AppProperties;
import com.example.metrics.entity.wsp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WspReaderImpl implements WspReader {

    private static final int METADATA_SIZE_IN_BYTES = 16;
    private static final int ARCHIVE_INFO_SIZE_IN_BYTES = 12;
    private static final int DATAPOINT_SIZE_IN_BYTES = 12;
    private static final String DATABASE_FILE_EXTENSION = ".wsp";

    @Autowired
    private AppProperties appProperties;

    public List<Series> getSeriesListBySeriesIds(List<String> seriesIds) throws IOException {
        return Files.walk(Paths.get(appProperties.getInputDataRootPath()))
                .filter(path -> path.getFileName().toString().toLowerCase().endsWith(DATABASE_FILE_EXTENSION))
                .map(this::getSeriesByWspFilePath)
                .collect(Collectors.toList());
    }

    private Series getSeriesByWspFilePath(Path path) {
        Series series = null;
        try (ReadableByteChannel byteChannel = Files.newByteChannel(path)) {
            Header header = getHeader(byteChannel);
            List<Archive> archives = getArchives(byteChannel, header.getArchiveInfos());
            series = new Series(path.toString(), header, archives); //todo
        } catch (IOException e) {
            e.printStackTrace();
        }
        return series;
    }

    private List<Archive> getArchives(ReadableByteChannel byteChannel, List<ArchiveInfo> archiveInfos) throws IOException {
        List<Archive> archives = new ArrayList<>();
        for (ArchiveInfo archiveInfo : archiveInfos) {
            archives.add(getArchive(byteChannel, archiveInfo));
        }
        return archives;
    }

    private Archive getArchive(ReadableByteChannel byteChannel, ArchiveInfo archiveInfo) throws IOException {
        int archiveSize = (int) (DATAPOINT_SIZE_IN_BYTES * archiveInfo.getPoints());
        ByteBuffer buf = ByteBuffer.allocate(archiveSize);
        byteChannel.read(buf);
        buf.rewind();
        Archive archive = new Archive(archiveInfo);
        do {
            int timestamp = buf.getInt();
            if (timestamp == 0) continue;
            double value = buf.getDouble();
            //if (value == 0.0) continue;
            Datapoint datapoint = new Datapoint(timestamp, value);
            archive.addDatapoint(datapoint);
        } while (buf.hasRemaining());

        return archive;
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
        return ((long) buf.getInt()) & 0xffffffffffffffffl; //todo
    }

}
