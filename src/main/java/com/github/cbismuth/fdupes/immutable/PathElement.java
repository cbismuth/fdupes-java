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

package com.github.cbismuth.fdupes.immutable;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class PathElement {

    private final Path path;
    private final BasicFileAttributes attributes;

    public PathElement(final Path path,
                       final BasicFileAttributes attributes) {
        this.path = path;
        this.attributes = attributes;
    }

    public Path getPath() {
        return path;
    }

    public long size() {
        return attributes.size();
    }

    public long creationTime() {
        return attributes.creationTime().toMillis();
    }

    public long lastModifiedTime() {
        return attributes.lastModifiedTime().toMillis();
    }

    public long lastAccessTime() {
        return attributes.lastAccessTime().toMillis();
    }

    @Override
    public String toString() {
        return path.toString();
    }

}