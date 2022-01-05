package io.jenkins.plugins.prism;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.Issue;

import hudson.FilePath;

import static hudson.Functions.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assumptions.*;

/**
 * Tests the class {@link FilePermissionEnforcer}.
 *
 * @author Ullrich Hafner
 */
class FilePermissionEnforcerTest {
    private static final FilePath WORKSPACE_UNIX = new FilePath(new File("/workspace"));
    private static final FilePath WORKSPACE_WINDOWS = new FilePath(new File("C:\\workspace"));

    @Test
    void shouldComparePathsOnUnix() {
        assumeThat(isWindows()).isFalse();

        FilePermissionEnforcer validator = new FilePermissionEnforcer();
        assertThat(validator.isInWorkspace("/a/b.c", WORKSPACE_UNIX, "/a")).isTrue();
        assertThat(validator.isInWorkspace("/a/b.c", WORKSPACE_UNIX, "/")).isTrue();

        assertThat(validator.isInWorkspace("/a/b.c", WORKSPACE_UNIX, "/a/b")).isFalse();
        assertThat(validator.isInWorkspace("/b/a/b.c", WORKSPACE_UNIX, "/a")).isFalse();
    }

    @Test
    void shouldAllowWorkspaceByDefaultOnUnix() {
        assumeThat(isWindows()).isFalse();

        FilePermissionEnforcer validator = new FilePermissionEnforcer();
        assertThat(validator.isInWorkspace("/workspace/b.c", WORKSPACE_UNIX, "/a")).isTrue();
        assertThat(validator.isInWorkspace("/b/workspace/b.c", WORKSPACE_UNIX, "/a")).isFalse();
        assertThat(validator.isInWorkspace("b.c", WORKSPACE_UNIX, "/a")).isFalse();
    }

    @Test @Issue("JENKINS-63782")
    void shouldComparePathsCaseInsensitiveOnWindows() {
        assumeThat(isWindows()).isTrue();

        FilePermissionEnforcer validator = new FilePermissionEnforcer();

        assertThat(validator.isInWorkspace("C:\\a\\b.c", WORKSPACE_WINDOWS, "C:\\a")).isTrue();
        assertThat(validator.isInWorkspace("C:\\a\\b.c", WORKSPACE_WINDOWS, "C:\\")).isTrue();
        assertThat(validator.isInWorkspace("C:\\a\\b.c", WORKSPACE_WINDOWS, "C:\\A")).isTrue();
        assertThat(validator.isInWorkspace("C:\\A\\b.c", WORKSPACE_WINDOWS, "C:\\a")).isTrue();
        assertThat(validator.isInWorkspace("c:\\a\\b.c", WORKSPACE_WINDOWS, "C:\\a")).isTrue();

        assertThat(validator.isInWorkspace("c:\\a\\b.c", WORKSPACE_WINDOWS, "C:\\b")).isFalse();
    }

    @Test
    void shouldAllowWorkspaceByDefaultOnWindows() {
        assumeThat(isWindows()).isTrue();

        FilePermissionEnforcer validator = new FilePermissionEnforcer();
        assertThat(validator.isInWorkspace("C:\\workspace\b.c", WORKSPACE_WINDOWS, "C:\\a")).isTrue();
        assertThat(validator.isInWorkspace("C:\\a\\workspace\\b.c", WORKSPACE_WINDOWS, "C:\\b")).isFalse();
        assertThat(validator.isInWorkspace("b.c", WORKSPACE_WINDOWS, "C:\\a")).isFalse();
    }
}
