/* OpenSource 2009 */
package org.devstat.gitdevstat.git.utils;

/** A NullProgressMonitor does not report progress anywhere. */
public class NullProgressMonitor implements ProgressMonitor {
    /** Immutable instance of a null progress monitor. */
    public static final NullProgressMonitor INSTANCE = new NullProgressMonitor();

    private NullProgressMonitor() {
        // Do not let others instantiate
    }

    /** {@inheritDoc} */
    @Override
    public void start(int totalTasks) {
        // Do not report.
    }

    /** {@inheritDoc} */
    @Override
    public void beginTask(String title, int totalWork) {
        // Do not report.
    }

    /** {@inheritDoc} */
    @Override
    public void update(int completed) {
        // Do not report.
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCancelled() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void endTask() {
        // Do not report.
    }

    @Override
    public void showDuration(boolean enabled) {
        // don't show
    }
}
