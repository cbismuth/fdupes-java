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

package fdupes.container;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileMetadataDuplicateWriter {

    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String WORKING_DIRECTORY = System.getProperty("user.dir");
    public static final String OUTPUT_FILENAME = "duplicates.log";

    public Path write(final Collection<String> inputPaths) throws IOException {
        final Path output = Paths.get(WORKING_DIRECTORY, OUTPUT_FILENAME);

        final String absolutePaths = inputPaths.stream().collect(Collectors.joining(NEW_LINE));

        Files.write(output, absolutePaths.getBytes(UTF_8));

        return output;
    }

}
