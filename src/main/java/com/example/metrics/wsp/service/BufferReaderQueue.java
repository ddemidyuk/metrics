package com.example.metrics.wsp.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BufferReaderQueue {
    private BlockingQueue<QueueElement> queue = new LinkedBlockingQueue<>();
    private Thread readThread;

    private Runnable readTask = () -> {
        try {
            while (true) {
                QueueElement reader = queue.take();
                ReadableByteChannel byteChannel = reader.getByteChannel();
                ByteBuffer buffer = ByteBuffer.allocate(reader.getBufferSize());
                byteChannel.read(buffer);
                reader.setBuffer(buffer);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    };

    public BufferReaderQueue() {
        readThread = new Thread(readTask);
        readThread.setDaemon(true);
        readThread.start();
    }

    public void offer(QueueElement reader) {
        queue.offer(reader);
    }

    public static final class QueueElement {
        private ReadableByteChannel byteChannel;
        private ByteBuffer buffer;
        private boolean dataIsNotRead = true;
        private int bufferSize;

        public QueueElement(ReadableByteChannel byteChannel, int bufferSize) {
            this.byteChannel = byteChannel;
            this.bufferSize = bufferSize;
        }

        synchronized
        public ByteBuffer getBuffer() {
            while (dataIsNotRead) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return buffer;
        }

        private int getBufferSize() {
            return bufferSize;
        }

        private ReadableByteChannel getByteChannel() {
            return byteChannel;
        }

        synchronized
        private void setBuffer(ByteBuffer buffer) {
            this.buffer = buffer;
            dataIsNotRead = false;
            notify();
        }
    }
}
