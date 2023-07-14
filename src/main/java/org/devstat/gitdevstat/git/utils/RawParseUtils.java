/* OpenSource 2008-2009 */
package org.devstat.gitdevstat.git.utils;

import static java.lang.System.getProperty;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.util.Arrays;
import org.slf4j.Logger;

public final class RawParseUtils {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(RawParseUtils.class);

    private static final byte[] digits10;

    static {
        digits10 = new byte['9' + 1];
        Arrays.fill(digits10, (byte) -1);
        for (char i = '0'; i <= '9'; i++) digits10[i] = (byte) (i - '0');
    }

    public static int parseBase10(final byte[] b, int ptr, final MutableInteger ptrResult) {
        int r = 0;
        int sign = 0;
        try {
            final int sz = b.length;
            while (ptr < sz && b[ptr] == ' ') ptr++;
            if (ptr >= sz) return 0;

            switch (b[ptr]) {
                case '-' -> {
                    sign = -1;
                    ptr++;
                }
                case '+' -> ptr++;
                default -> {
                    // nop
                }
            }

            while (ptr < sz) {
                final byte v = digits10[b[ptr]];
                if (v < 0) break;
                r = (r * 10) + v;
                ptr++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Not a valid digit.
        }
        if (ptrResult != null) ptrResult.value = ptr;
        return sign < 0 ? -r : r;
    }

    public static int next(byte[] b, int ptr, char chrA) {
        final int sz = b.length;
        while (ptr < sz) {
            if (b[ptr++] == chrA) return ptr;
        }
        return ptr;
    }

    public static int nextLF(byte[] b, int ptr) {
        return next(b, ptr, '\n');
    }

    public static String decode(
            final Charset cs, final byte[] buffer, final int start, final int end) {
        try {
            return decodeNoFallback(cs, buffer, start, end);
        } catch (CharacterCodingException e) {
            // Fall back to an ISO-8859-1 style encoding. At least all of
            // the bytes will be present in the output.
            //
            return extractBinaryString(buffer, start, end);
        }
    }

    public static String decodeNoFallback(
            final Charset cs, final byte[] buffer, final int start, final int end)
            throws CharacterCodingException {
        ByteBuffer b = ByteBuffer.wrap(buffer, start, end - start);
        b.mark();

        // Try our built-in favorite. The assumption here is that
        // decoding will fail if the data is not actually encoded
        // using that encoder.
        try {
            return decode(b, UTF_8);
        } catch (CharacterCodingException e) {
            b.reset();
        }

        if (!cs.equals(UTF_8)) {
            // Try the suggested encoding, it might be right since it was
            // provided by the caller.
            try {
                return decode(b, cs);
            } catch (CharacterCodingException e) {
                b.reset();
            }
        }

        Charset defcs = getDefaultCharset();
        if (!defcs.equals(cs) && !defcs.equals(UTF_8)) {
            try {
                return decode(b, defcs);
            } catch (CharacterCodingException e) {
                b.reset();
            }
        }

        throw new CharacterCodingException();
    }

    public static String extractBinaryString(final byte[] buffer, final int start, final int end) {
        final StringBuilder r = new StringBuilder(end - start);
        for (int i = start; i < end; i++) r.append((char) (buffer[i] & 0xff));
        return r.toString();
    }

    private static String decode(ByteBuffer b, Charset charset) throws CharacterCodingException {
        final CharsetDecoder d = charset.newDecoder();
        d.onMalformedInput(CodingErrorAction.REPORT);
        d.onUnmappableCharacter(CodingErrorAction.REPORT);
        return d.decode(b).toString();
    }

    private static Charset defaultCharset;

    private static Charset getDefaultCharset() {
        Charset result = defaultCharset;
        if (result == null) {
            // JEP 400: Java 18 populates this system property.
            String encoding = getProperty("native.encoding"); // $NON-NLS-1$
            try {
                if (encoding != null && !"".equals(encoding)) {
                    result = Charset.forName(encoding);
                }
            } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
                log.error("Error gettig default charset", e);
            }
            if (result == null) {
                // This is always UTF-8 on Java >= 18.
                result = Charset.defaultCharset();
            }
            defaultCharset = result;
        }
        return result;
    }

    private RawParseUtils() {
        // Don't create instances of a static only utility.
    }
}
