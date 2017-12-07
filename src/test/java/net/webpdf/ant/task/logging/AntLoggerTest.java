package net.webpdf.ant.task.logging;

import net.webpdf.ant.task.Task;
import net.webpdf.ant.task.TaskName;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class AntLoggerTest {

    private AntLogger logger;
    private String logMsg;
    private Throwable logT;
    private int logMsgLevel;
    private Location logLocation;

    @Before
    public void prepare() {
        logger = new AntLogger(new Task(TaskName.OPERATION) {
            @Override
            public void execute() throws BuildException {
                //DO NOTHING
            }

            @Override
            public void log(Throwable t, int msgLevel) {
                logMsg = t.getMessage();
                logT = t;
                logMsgLevel = msgLevel;
            }

            @Override
            public void log(String msg, int msgLevel) {
                logMsg = msg;
                logT = null;
                logMsgLevel = msgLevel;
            }

            @Override
            public void log(String msg) {
                logMsg = msg;
                logT = null;
                logMsgLevel = -1;
            }

            @Override
            public void log(String msg, Throwable t, int msgLevel) {
                logMsg = msg;
                logT = t;
                logMsgLevel = msgLevel;
            }

            @Override
            public Location getLocation() {
                logLocation = new Location("file", 10, 20);
                return logLocation;
            }
        });
    }

    @Test
    public void testInfoEmptyMsg() {
        logger.info(LogTag.OPERATION);
        assertEquals("[OPERATION] \n    [LOCATION] file:10: ", logMsg);
        assertEquals(null, logT);
        assertEquals(Project.MSG_INFO, logMsgLevel);
    }

    @Test
    public void testInfo() {
        logger.info("msg", LogTag.OPERATION);
        assertEquals("[OPERATION] msg\n    [LOCATION] file:10: ", logMsg);
        assertEquals(null, logT);
        assertEquals(Project.MSG_INFO, logMsgLevel);
    }

    @Test
    public void testWarnThrowableOnly() {
        Exception ex = new IOException("io error");
        logger.warn(ex, LogTag.OPERATION);
        assertEquals("[WARN] [OPERATION] io error\n    [LOCATION] file:10: ", logMsg);
        assertEquals(ex, logT);
        assertEquals(Project.MSG_WARN, logMsgLevel);
    }

    @Test
    public void testWarnMsgOnly() {
        logger.warn("io error", LogTag.OPERATION);
        assertEquals("[WARN] [OPERATION] io error\n    [LOCATION] file:10: ", logMsg);
        assertEquals(null, logT);
        assertEquals(Project.MSG_WARN, logMsgLevel);
    }

    @Test
    public void testWarn() {
        Exception ex = new IOException("io error");
        logger.warn("error message", ex, LogTag.OPERATION);
        assertEquals("[WARN] [OPERATION] error message\n    [LOCATION] file:10: ", logMsg);
        assertEquals(ex, logT);
        assertEquals(Project.MSG_WARN, logMsgLevel);
    }

    @Test
    public void testErrorThrowableOnly() throws Exception {
        Exception ex = new IOException("io error");
        logger.error(ex, LogTag.OPERATION);
        try (
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter)) {
            ex.printStackTrace(printWriter);
            assertEquals("[ERROR] [OPERATION] io error\n" + stringWriter.toString() + "\n    [LOCATION] file:10: ", logMsg);
            assertEquals(ex, logT);
            assertEquals(Project.MSG_ERR, logMsgLevel);
        }
    }

    @Test
    public void testErrorMessageOnly() throws Exception {
        logger.error("errorMessage", LogTag.OPERATION);
        assertEquals("[ERROR] [OPERATION] errorMessage\n    [LOCATION] file:10: ", logMsg);
        assertEquals(null, logT);
        assertEquals(Project.MSG_ERR, logMsgLevel);
    }

    @Test
    public void testError() throws Exception {
        Exception ex = new IOException("io error");
        logger.error("errorMessage", ex, LogTag.OPERATION);
        try (
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter)) {
            ex.printStackTrace(printWriter);
            assertEquals("[ERROR] [OPERATION] errorMessage\n" + stringWriter.toString() + "\n    [LOCATION] file:10: ", logMsg);
            assertEquals(ex, logT);
            assertEquals(Project.MSG_ERR, logMsgLevel);
        }
    }

    @Test
    public void testNullMessage() throws Exception {
        Exception ex = new IOException("io error");
        logger.error(ex, LogTag.OPERATION);
        try (
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter)) {
            ex.printStackTrace(printWriter);
            assertEquals("[ERROR] [OPERATION] io error\n" + stringWriter.toString() + "\n    [LOCATION] file:10: ", logMsg);
            assertEquals(ex, logT);
            assertEquals(Project.MSG_ERR, logMsgLevel);
        }
    }

    @Test
    public void testInnerMessage() throws Exception {
        Exception ex = new IOException(null, new IOException("io error"));
        logger.error(ex, LogTag.OPERATION);
        try (
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter)) {
            ex.printStackTrace(printWriter);
            assertEquals("[ERROR] [OPERATION] io error\n" + stringWriter.toString() + "\n    [LOCATION] file:10: ", logMsg);
            assertEquals(ex, logT);
            assertEquals(Project.MSG_ERR, logMsgLevel);
        }
    }

    @Test
    public void testNullThrowable() throws Exception {
        Exception ex = null;
        logger.error(ex, LogTag.OPERATION);
        try (
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter)) {
            assertEquals("[ERROR] [OPERATION] \n    [LOCATION] file:10: ", logMsg);
            assertEquals(ex, logT);
            assertEquals(Project.MSG_ERR, logMsgLevel);
        }
    }
}
