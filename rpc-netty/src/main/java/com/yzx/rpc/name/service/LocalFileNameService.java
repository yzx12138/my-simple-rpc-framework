package com.yzx.rpc.name.service;

import com.yzx.rpc.serialize.SerializeSupport;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author baozi
 * @Description: 本地临时文件实现注册中心
 * @Date created on 2022/7/12
 */
public class LocalFileNameService implements NameService {

    private File file = null;
    private Collection<String> schemes = Collections.singleton(NameServiceSchemes.FILE.getDesc());
    //private Random random = new Random();

    @Override
    public void registerService(String serviceName, URI uri) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel fileChannel = raf.getChannel()) {
            FileLock lock = fileChannel.lock();
            try {
                int fileLength = (int) raf.length();
                MetaData metaData;
                byte[] bytes;
                if (fileLength > 0) {
                    bytes = new byte[fileLength];
                    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                    while (byteBuffer.hasRemaining()) {
                        fileChannel.read(byteBuffer);
                    }

                    metaData = SerializeSupport.parse(bytes);
                } else {
                    metaData = new MetaData();
                }

                List<URI> uris = metaData.computeIfAbsent(serviceName, k -> new ArrayList<>());
                if (!uris.contains(uri)) {
                    uris.add(uri);
                }

                bytes = SerializeSupport.serialize(metaData);
                fileChannel.truncate(bytes.length);
                fileChannel.position(0L);
                fileChannel.write(ByteBuffer.wrap(bytes));
                fileChannel.force(true);
            } finally {
                lock.release();
            }
        }
    }

    @Override
    public URI lookupService(String serviceName) throws IOException {
        MetaData metaData;
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel fileChannel = raf.getChannel()) {
            FileLock fileLock = fileChannel.lock();
            try {
                byte[] bytes = new byte[(int) raf.length()];
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                while (byteBuffer.hasRemaining()) {
                    fileChannel.read(byteBuffer);
                }
                metaData = bytes.length == 0 ? new MetaData() : SerializeSupport.parse(bytes);
            } finally {
                fileLock.release();
            }
        }

        List<URI> uris = metaData.get(serviceName);
        if (uris == null || uris.size() == 0) {
            return null;
        }
        // 随机负载均衡
        return uris.get(ThreadLocalRandom.current().nextInt(uris.size()));
    }

    @Override
    public Collection<String> supportedSchemes() {
        return schemes;
    }

    /**
     * 连接注册中心
     *
     * @param nameServiceUri 对于本地文件实现的注册中心，每个文件就是一个注册中心实例
     */
    @Override
    public void connect(URI nameServiceUri) {
        if (!schemes.contains(nameServiceUri.getScheme())) {
            throw new IllegalArgumentException("unSupport scheme");
        }
        if (file == null) {
            file = new File(nameServiceUri);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
