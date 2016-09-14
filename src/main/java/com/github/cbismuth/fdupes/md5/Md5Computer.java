/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Christophe Bismuth
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.cbismuth.fdupes.md5;

import com.codahale.metrics.Timer;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.primitives.UnsignedBytes;
import org.slf4j.Logger;
import org.zeroturnaround.exec.ProcessExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static com.codahale.metrics.MetricRegistry.name;
import static com.github.cbismuth.fdupes.metrics.MetricRegistrySingleton.getMetricRegistry;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.slf4j.LoggerFactory.getLogger;

public class Md5Computer {

    private static final Logger LOGGER = getLogger(Md5Computer.class);

    private final Optional<String> binaryName;

    public Md5Computer() {
        this(new OpenSslChecker().getBinaryName());
    }

    public Md5Computer(final String binaryName) {
        this.binaryName = Optional.ofNullable(binaryName);
    }

    public String compute(final Path path) {
        Preconditions.checkNotNull(path, "null file metadata");

        try (final Timer.Context ignored = getMetricRegistry().timer(name("md5", "timer")).time()) {
            return doIt(path);
        } catch (final Exception e) {
            LOGGER.error("Can't compute MD5 from file [{}] ([{}]: [{}])",
                         path, e.getClass().getSimpleName(), e.getMessage());

            return randomUUID().toString();
        }
    }

    private String doIt(final Path path) {
        if (binaryName.isPresent()) {
            return nativeMd5(path);
        } else {
            return jvmMd5(path);
        }
    }

    public String jvmMd5(final Path path) {
        Preconditions.checkNotNull(path, "null file metadata");

        try {
            final String separator = ":";
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] bytes = Files.readAllBytes(path);
            final byte[] digest = md.digest(bytes);

            return UnsignedBytes.join(separator, digest);
        } catch (final Throwable e) {
            throw Throwables.propagate(e);
        }
    }

    public String nativeMd5(final Path path) {
        Preconditions.checkNotNull(path, "null file metadata");

        try {
            return new ProcessExecutor().command(getNativeMd5Command(path))
                                        .readOutput(true)
                                        .execute()
                                        .outputString()
                                        .split("\\s")[1];
        } catch (final Throwable e) {
            throw Throwables.propagate(e);
        }
    }

    private Iterable<String> getNativeMd5Command(final Path path) {
        final Collection<String> command = newArrayList();

        if (binaryName.isPresent() && Objects.equals("openssl", binaryName.get())) {
            command.add("openssl");
            command.add("md5");
        } else {
            throw new UnsupportedOperationException(format("Unsupported binary name [%s]!", binaryName));
        }

        command.add(path.toString());

        return command;
    }

    @Override
    public String toString() {
        return binaryName.isPresent() ? "md5-native" : "md5-jvm";
    }

}
