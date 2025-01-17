// This file is part of the WhileLang Compiler (wlc).
//
// The WhileLang Compiler is free software; you can redistribute
// it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// The WhileLang Compiler is distributed in the hope that it
// will be useful, but WITHOUT ANY WARRANTY; without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE. See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public
// License along with the WhileLang Compiler. If not, see
// <http://www.gnu.org/licenses/>
//
// Copyright 2013, David James Pearce.

package whilelang.util;

import whilelang.ast.Attribute;

import java.io.*;

/**
 * This exception is thrown when a syntax error occurs in the parser.
 *
 * @author David Pearce
 */
public class SyntaxError extends RuntimeException {

    private String msg;
    private String filename;
    private int start;
    private int end;

    /**
     * Identify a syntax error at a particular point in a file.
     *
     * @param msg      Message detailing the problem.
     * @param filename The source file that this error is referring to.
     * @param line     Line number within file containing problem.
     * @param column   Column within line of file containing problem.
     */
    public SyntaxError(String msg, String filename, int start, int end) {
        this.msg = msg;
        this.filename = filename;
        this.start = start;
        this.end = end;
    }

    /**
     * Identify a syntax error at a particular point in a file.
     *
     * @param msg      Message detailing the problem.
     * @param filename The source file that this error is referring to.
     * @param line     Line number within file containing problem.
     * @param column   Column within line of file containing problem.
     */
    public SyntaxError(String msg, String filename, int start, int end,
                       Throwable ex) {
        super(ex);
        this.msg = msg;
        this.filename = filename;
        this.start = start;
        this.end = end;
    }

    public String getMessage() {
        if (msg != null) {
            return msg;
        } else {
            return "";
        }
    }

    /**
     * Error message
     *
     * @return
     */
    public String msg() {
        return msg;
    }

    /**
     * Filename for file where the error arose.
     *
     * @return
     */
    public String filename() {
        return filename;
    }

    /**
     * Get index of first character of offending location.
     *
     * @return
     */
    public int start() {
        return start;
    }

    /**
     * Get index of last character of offending location.
     *
     * @return
     */
    public int end() {
        return end;
    }

    /**
     * Output the syntax error to a given output stream.
     */
    public void outputSourceError(PrintStream output) {
        if (filename == null) {
            output.println("syntax error: " + getMessage());
        } else {
            int line = 0;
            int lineStart = 0;
            int lineEnd = 0;
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        new FileInputStream(filename), "UTF-8"));

                // first, read whole file
                int len = 0;
                char[] buf = new char[1024];
                while ((len = in.read(buf)) != -1) {
                    text.append(buf, 0, len);
                }

                while (lineEnd < text.length() && lineEnd <= start) {
                    lineStart = lineEnd;
                    lineEnd = parseLine(text, lineEnd);
                    line = line + 1;
                }
            } catch (IOException e) {
                output.println("syntax error: " + getMessage());
                return;
            }
            lineEnd = Math.min(lineEnd, text.length());

            output.println(filename + ":" + line + ": " + getMessage());
            // NOTE: in the following lines I don't print characters
            // individually. The reason for this is that it messes up the ANT
            // task output.
            String str = "";
            for (int i = lineStart; i < lineEnd; ++i) {
                str = str + text.charAt(i);
            }
            if (str.length() > 0 && str.charAt(str.length() - 1) == '\n') {
                output.print(str);
            } else {
                // this must be the very last line of output and, in this
                // particular case, there is no new-line character provided.
                // Therefore, we need to provide one ourselves!
                output.println(str);
            }
            str = "";
            for (int i = lineStart; i < start; ++i) {
                if (text.charAt(i) == '\t') {
                    str += "\t";
                } else {
                    str += " ";
                }
            }
            for (int i = start; i <= end; ++i) {
                str += "^";
            }
            output.println(str);
        }
    }

    private static int parseLine(StringBuilder text, int index) {
        while (index < text.length() && text.charAt(index) != '\n') {
            index++;
        }
        return index + 1;
    }

    public static final long serialVersionUID = 1l;

    public static void syntaxError(String msg, String filename,
                                   SyntacticElement elem) {
        int start = -1;
        int end = -1;

        Attribute.Source attr = (Attribute.Source) elem
                .attribute(Attribute.Source.class);
        if (attr != null) {
            start = attr.start;
            end = attr.end;
        }

        throw new SyntaxError(msg, filename, start, end);
    }

    public static void syntaxError(String msg, String filename,
                                   SyntacticElement elem, Throwable ex) {
        int start = -1;
        int end = -1;

        Attribute.Source attr = (Attribute.Source) elem
                .attribute(Attribute.Source.class);
        if (attr != null) {
            start = attr.start;
            end = attr.end;
        }

        throw new SyntaxError(msg, filename, start, end, ex);
    }

    /**
     * An internal failure is a special form of syntax error which indicates
     * something went wrong whilst processing some piece of syntax. In other
     * words, is an internal error in the compiler, rather than a mistake in the
     * input program.
     *
     * @author David J. Pearce
     */
    public static class InternalFailure extends SyntaxError {
        public InternalFailure(String msg, String filename, int start, int end) {
            super(msg, filename, start, end);
        }

        public InternalFailure(String msg, String filename, int start, int end,
                               Throwable ex) {
            super(msg, filename, start, end, ex);
        }

        public String getMessage() {
            String msg = super.getMessage();
            if (msg == null || msg.equals("")) {
                return "internal failure";
            } else {
                return "internal failure, " + msg;
            }
        }
    }

    public static void internalFailure(String msg, String filename,
                                       SyntacticElement elem) {
        int start = -1;
        int end = -1;

        Attribute.Source attr = (Attribute.Source) elem.attribute(Attribute.Source.class);
        if (attr != null) {
            start = attr.start;
            end = attr.end;
        }

        throw new InternalFailure(msg, filename, start, end);
    }

    public static void internalFailure(String msg, String filename,
                                       SyntacticElement elem, Throwable ex) {
        int start = -1;
        int end = -1;

        Attribute.Source attr = (Attribute.Source) elem.attribute(Attribute.Source.class);
        if (attr != null) {
            start = attr.start;
            end = attr.end;
        }

        throw new InternalFailure(msg, filename, start, end, ex);
    }
}
