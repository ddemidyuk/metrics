package com.example.metrics.wsp.service;

import com.example.metrics.wsp.entities.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class WspReaderImpl implements WspReader {

    private static final int METADATA_SIZE_IN_BYTES = 16;
    private static final int ARCHIVE_INFO_SIZE_IN_BYTES = 12;
    private static final int DATAPOINT_SIZE_IN_BYTES = 12;
    private static final String DATABASE_FILE_EXTENSION = ".wsp";

    public Series getSeriesByWspFilePath(Params params) {
        Series series = null;
        Path path = Paths.get(params.getRootPath() + params.getSeriesId() + DATABASE_FILE_EXTENSION);
        try (ReadableByteChannel byteChannel = Files.newByteChannel(path)) {
            Header header = getHeader(byteChannel, params.getFilter());
            List<Archive> archives = getArchives(byteChannel, header.getArchiveInfos(), params.getFilter());
            series = new Series(params.getSeriesId(), header, archives);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return series;
    }

    private List<Archive> getArchives(ReadableByteChannel byteChannel, List<ArchiveInfo> archiveInfos, Filter filter) throws IOException {
        List<Archive> archives = new ArrayList<>();
        for (ArchiveInfo archiveInfo : archiveInfos) {
            archives.add(getArchive(byteChannel, archiveInfo, filter));
        }
        return archives;
    }

    private Archive getArchive(ReadableByteChannel byteChannel, ArchiveInfo archiveInfo, Filter filter) throws IOException {
        int archiveSize = DATAPOINT_SIZE_IN_BYTES * archiveInfo.getPoints();
        ByteBuffer buf = ByteBuffer.allocate(archiveSize);
        byteChannel.read(buf);
        buf.rewind();

        //todo values of points will be incorrect  cause  points will have filtered
        Archive archive = filter.getDatapointComparator() != null ?
                new Archive(archiveInfo, filter.getDatapointComparator()) :
                new Archive(archiveInfo);

        do {
            int timestamp = buf.getInt();
            double value = buf.getDouble();
            if (filter.getTimestampPredicate().negate().test(timestamp)) continue;
            if (filter.getValuePredicate().negate().test(value)) continue;
            Datapoint datapoint = new Datapoint(timestamp, value);
            archive.addDatapoint(datapoint);
        } while (buf.hasRemaining());

        return archive;
    }

    private Header getHeader(ReadableByteChannel byteChannel, Filter filter) throws IOException {
        Metadata metadata = getMetadata(byteChannel);
        List<ArchiveInfo> archiveInfos = getArchiveInfos(byteChannel, metadata.getArchiveCount(), filter);
        return new Header(metadata, archiveInfos);
    }

    private Metadata getMetadata(ReadableByteChannel byteChannel) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(METADATA_SIZE_IN_BYTES);
        byteChannel.read(buf);
        buf.rewind();
        return Metadata.Builder.newInstance()
                .aggregationType(buf.getInt())
                .maxRetention(getUnsignedLongFrom4Bytes(buf))
                .xFilesFactor(buf.getFloat())
                .archiveCount(buf.getInt())
                .build();
    }

    private List<ArchiveInfo> getArchiveInfos(ReadableByteChannel byteChannel, int archiveCount, Filter filter) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(ARCHIVE_INFO_SIZE_IN_BYTES * archiveCount);
        byteChannel.read(buf);
        buf.rewind();
        List<ArchiveInfo> archiveInfos = new ArrayList<>();
        for (int i = 1; i <= archiveCount; i++) {
            ArchiveInfo archiveInfo = ArchiveInfo.Builder.newInstance()
                    .offset(buf.getInt())
                    .secondsPerPoint(buf.getInt())
                    .points(buf.getInt())
                    .build();
            if (filter.getSecondsPerPointPredicate().test(archiveInfo.getSecondsPerPoint())) {
                archiveInfos.add(archiveInfo);
            }

        }
        return archiveInfos;
    }

    private long getUnsignedLongFrom4Bytes(ByteBuffer buf) {
        return ((long) buf.getInt()) & 0xffffffffffffffffl; //todo
    }

}
